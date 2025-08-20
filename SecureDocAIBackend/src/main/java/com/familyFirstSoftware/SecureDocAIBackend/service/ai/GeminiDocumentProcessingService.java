package com.familyFirstSoftware.SecureDocAIBackend.service.ai;

import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for processing documents with AI, handling text extraction, chunking, and summarization.
 * Features:
 * - Supports PDF, DOCX, DOC, TXT, RTF
 * - Smart chunking with paragraph/sentence awareness
 * - Context preservation with overlap
 * - Graceful handling of large documents
 */
@Service
@Slf4j
public class GeminiDocumentProcessingService implements DocumentProcessingService {

    // File handling constants
    private static final long MAX_FILE_SIZE = 45 * 1024 * 1024; // 45MB
    private static final int MAX_FILE_COUNT = 3;
    
    // Supported MIME types
    private static final List<String> SUPPORTED_CONTENT_TYPES = Arrays.asList(
        "application/pdf",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX
        "application/msword", // DOC
        "text/plain", // TXT
        "application/rtf" // RTF
    );

    // Chunking settings (in characters, not tokens for simplicity)
    private static final int TARGET_CHUNK_SIZE = 100000; // ~25K tokens
    private static final int MIN_CHUNK_SIZE = 10000;    // Don't make chunks smaller than this
    private static final int OVERLAP_SIZE = 2000;       // Overlap between chunks in chars
    private static final int MAX_CHUNK_SIZE = 200000;   // Hard limit to avoid OOM
    
    // Text processing patterns
    private static final Pattern PARAGRAPH_SPLIT = Pattern.compile("\\n\\s*\\n");
    private static final Pattern SENTENCE_TERMINATORS = Pattern.compile("(?<=[.!?]\\s+)");
    
    private final AiService aiService;
    private final Tika tika;

    public GeminiDocumentProcessingService(AiService aiService) {
        this.aiService = aiService;
        this.tika = new Tika();
    }

    @Override
    public String processDocument(MultipartFile file) {
        validateFile(file);
        String content = extractText(file);
        if (content.trim().isEmpty()) {
            throw new ApiException("The document appears to be empty or contains no readable text.");
        }
        return processContentInChunks(content);
    }

    @Override
    public String processDocuments(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new ApiException("No files provided for processing.");
        }
        if (files.size() > MAX_FILE_COUNT) {
            throw new ApiException("Cannot process more than " + MAX_FILE_COUNT + " files at a time.");
        }
        
        files.forEach(this::validateFile);
        
        // Process each file and collect non-empty content with file markers
        List<String> fileContents = new ArrayList<>();
        for (MultipartFile file : files) {
            String content = extractText(file).trim();
            if (!content.isEmpty()) {
                fileContents.add("\n[Document: " + file.getOriginalFilename() + "]\n" + content);
            }
        }
        
        if (fileContents.isEmpty()) {
            throw new ApiException("None of the provided documents contain readable text.");
        }
        
        return processContentInChunks(String.join("\n\n---\n\n", fileContents));
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException("File is empty.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApiException("File size exceeds the limit of 45MB.");
        }
        try {
            String contentType = tika.detect(file.getInputStream());
            if (!SUPPORTED_CONTENT_TYPES.contains(contentType)) {
                throw new ApiException("File type not supported: " + contentType);
            }
        } catch (IOException e) {
            throw new ApiException("Failed to determine file type.", e);
        }
    }

    private String extractText(MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            BodyContentHandler handler = new BodyContentHandler(-1); // -1 to disable write limit
            Metadata metadata = new Metadata();
            ParseContext pcontext = new ParseContext();
            AutoDetectParser parser = new AutoDetectParser();
            parser.parse(stream, handler, metadata, pcontext);
            return handler.toString();
        } catch (IOException | TikaException | SAXException e) {
            log.error("Error extracting text from file: {}", file.getOriginalFilename(), e);
            throw new ApiException("Failed to extract text from the document.", e);
        }
    }

    /**
     * Processes content in chunks with smart boundaries and overlap
     */
    private String processContentInChunks(String content) {
        List<String> chunks = chunkContentIntelligently(content);
        log.info("Split content into {} chunks for processing", chunks.size());
        
        // Process each chunk with context
        List<String> chunkSummaries = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            boolean isFirst = (i == 0);
            boolean isLast = (i == chunks.size() - 1);
            
            String prompt = buildChunkPrompt(chunk, i + 1, chunks.size(), isFirst, isLast);
            
            try {
                String summary = aiService.generateResponse(prompt);
                chunkSummaries.add(summary);
                log.debug("Processed chunk {}/{}", i + 1, chunks.size());
            } catch (Exception e) {
                log.error("Error processing chunk {}/{}", i + 1, chunks.size(), e);
                throw new ApiException("Failed to process document chunk " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        // If only one chunk, return its summary directly
        if (chunkSummaries.size() == 1) {
            return chunkSummaries.get(0);
        }
        
        // Otherwise, combine summaries
        return combineChunkSummaries(chunkSummaries);
    }
    
    /**
     * Builds an appropriate prompt for a chunk based on its position
     */
    private String buildChunkPrompt(String chunk, int chunkNum, int totalChunks, boolean isFirst, boolean isLast) {
        StringBuilder prompt = new StringBuilder();
        
        // System context
        prompt.append("You are a helpful AI assistant that summarizes medical documents. ")
             .append("Provide a clear, concise summary of the following document section.\n\n");
        
        // Chunk context
        if (totalChunks > 1) {
            prompt.append("This is section ").append(chunkNum).append(" of ").append(totalChunks);
            
            if (!isFirst) prompt.append(" (continued from previous section)");
            if (isLast) prompt.append(" (final section)");
            
            prompt.append(". The content may start or end mid-sentence.\n\n");
        }
        
        // Content instructions
        prompt.append("Focus on key medical information, findings, and recommendations. ")
             .append("Maintain all critical details but be concise.\n\n")
             .append("DOCUMENT SECTION:");
        
        // The actual chunk content
        prompt.append("\n\n").append(chunk).append("\n\n");
        
        // Response instructions
        prompt.append("SUMMARY:")
             .append("\nProvide a well-structured summary with these sections (if applicable):\n")
             .append("- Key Findings\n")
             .append("- Important Details\n")
             .append("- Recommended Actions\n")
             .append("\nBe thorough but concise. Avoid introductory phrases or disclaimers.");
        
        return prompt.toString();
    }
    
    /**
     * Combines multiple chunk summaries into a final coherent summary
     */
    private String combineChunkSummaries(List<String> chunkSummaries) {
        String combined = String.join("\n\n---\n\n", chunkSummaries);
        
        // If the combined size is small, just return it
        if (combined.length() < TARGET_CHUNK_SIZE) {
            return combined;
        }
        
        // Otherwise, ask the AI to consolidate
        String prompt = "Combine these document section summaries into one cohesive summary. " +
                       "Remove any redundancy. Organize by topic rather than by original section.\n\n" +
                       combined + "\n\n" +
                       "FINAL COMBINED SUMMARY:";
        
        return aiService.generateResponse(prompt);
    }
    
    /**
     * Splits content into chunks respecting sentence and paragraph boundaries
     */
    private List<String> chunkContentIntelligently(String content) {
        // Normalize whitespace and clean up the content
        content = content.replaceAll("\\s+", " ").trim();
        
        // First try to split by paragraphs
        List<String> paragraphs = Arrays.stream(PARAGRAPH_SPLIT.split(content))
                .map(String::trim)
                .filter(p -> !p.isEmpty())
                .collect(Collectors.toList());
        
        // If the entire content fits in one chunk, return it
        if (content.length() <= TARGET_CHUNK_SIZE) {
            return Collections.singletonList(content);
        }
        
        // If we have paragraphs, chunk them intelligently
        if (!paragraphs.isEmpty()) {
            return chunkParagraphs(paragraphs);
        }
        
        // Fall back to sentence-level chunking for non-paragraph text
        return chunkBySentences(content);
    }
    
    /**
     * Chunks paragraphs into coherent sections
     */
    private List<String> chunkParagraphs(List<String> paragraphs) {
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        int currentSize = 0;
        
        for (String paragraph : paragraphs) {
            // If adding this paragraph would exceed the target size (with some buffer)
            if (currentSize > 0 && (currentSize + paragraph.length() > TARGET_CHUNK_SIZE)) {
                // If we already have content, finalize this chunk
                if (currentSize > 0) {
                    chunks.add(currentChunk.toString());
                    currentChunk = new StringBuilder();
                    currentSize = 0;
                    
                    // Add overlap from the end of the previous chunk
                    if (!chunks.isEmpty() && chunks.get(chunks.size() - 1).length() > OVERLAP_SIZE) {
                        String lastChunk = chunks.get(chunks.size() - 1);
                        String overlap = lastChunk.substring(Math.max(0, lastChunk.length() - OVERLAP_SIZE));
                        currentChunk.append(overlap).append("\n\n");
                        currentSize = overlap.length() + 2;
                    }
                }
            }
            
            // Add the paragraph to the current chunk
            if (currentSize > 0) {
                currentChunk.append("\n\n");
                currentSize += 2;
            }
            currentChunk.append(paragraph);
            currentSize += paragraph.length();
        }
        
        // Add the last chunk if it's not empty
        if (currentSize > 0) {
            chunks.add(currentChunk.toString());
        }
        
        return chunks;
    }
    
    /**
     * Fallback chunking by sentences when paragraphs aren't available
     */
    private List<String> chunkBySentences(String content) {
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        int currentSize = 0;
        
        // Split by sentence terminators but keep them in the result
        String[] sentences = SENTENCE_TERMINATORS.split(content);
        
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.isEmpty()) continue;
            
            // If adding this sentence would exceed the target size (with some buffer)
            if (currentSize > 0 && (currentSize + sentence.length() > TARGET_CHUNK_SIZE)) {
                if (currentSize > 0) {
                    chunks.add(currentChunk.toString());
                    currentChunk = new StringBuilder();
                    currentSize = 0;
                    
                    // Add overlap from the end of the previous chunk
                    if (!chunks.isEmpty() && chunks.get(chunks.size() - 1).length() > OVERLAP_SIZE) {
                        String lastChunk = chunks.get(chunks.size() - 1);
                        String overlap = lastChunk.substring(Math.max(0, lastChunk.length() - OVERLAP_SIZE));
                        currentChunk.append(overlap).append(" ");
                        currentSize = overlap.length() + 1;
                    }
                }
            }
            
            // Add the sentence to the current chunk
            if (currentSize > 0) {
                currentChunk.append(" ");
                currentSize++;
            }
            currentChunk.append(sentence);
            currentSize += sentence.length();
        }
        
        // Add the last chunk if it's not empty
        if (currentSize > 0) {
            chunks.add(currentChunk.toString());
        }
        
        return chunks;
    }

    /**
     * @deprecated Replaced by chunkContentIntelligently which provides better text segmentation
     */
    @Deprecated
    private List<String> chunkText(String text) {
        // Fallback simple chunking if needed
        return Collections.singletonList(text);
    }
}
