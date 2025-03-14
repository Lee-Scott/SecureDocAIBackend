package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.dto.Document;
import com.familyFirstSoftware.SecureDocAIBackend.dto.api.IDocument;
import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentEntity;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.DocumentRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.DocumentService;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import com.familyFirstSoftware.SecureDocAIBackend.utils.DocumentUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.FILE_STORAGE;
import static com.familyFirstSoftware.SecureDocAIBackend.utils.DocumentUtils.fromDocumentEntity;
import static com.familyFirstSoftware.SecureDocAIBackend.utils.DocumentUtils.getDocumentUri;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static javax.swing.text.StyleConstants.setIcon;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.springframework.util.StringUtils.cleanPath;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 3/12/2025
 */

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public Page<IDocument> getDocuments(int page, int size) {
        return documentRepository.findDocuments(PageRequest.of(page, size, Sort.by("name"))); // Can sort by any field
    }

    @Override
    public Page<IDocument> getDocuments(int page, int size, String name) {
        return null;
    }

    @Override
    public Collection<Document> saveDocument(String userId, List<MultipartFile> documents) {
        List<Document> newDocuments = new ArrayList<>();
        var userEntity = userRepository.findUserByUserId(userId).get();
        var storage = Paths.get(FILE_STORAGE).toAbsolutePath().normalize();
        try {
            for(MultipartFile document : documents){
                var fileName = cleanPath(Objects.requireNonNull(document.getOriginalFilename()));
                if("..".contains(fileName)){ // Todo: use a library for this
                    throw new ApiException(String.format("Invalid file name %s", fileName));
                }
                var documentEntity = DocumentEntity
                        .builder()
                        .documentId(UUID.randomUUID().toString())
                        .name(fileName)
                        .owner(userEntity)
                        .extension(getExtension(fileName))
                        .uri(getDocumentUri(fileName))
                        .formattedSize(byteCountToDisplaySize(document.getSize()))
                        .icon(DocumentUtils.setIcon(getExtension(fileName)))
                        .build();
                var savedDocument = documentRepository.save(documentEntity);
                Files.copy(document.getInputStream(), storage.resolve(fileName), REPLACE_EXISTING); //Todo call google drive here or amazon s3
                Document newDocument = fromDocumentEntity(savedDocument, userService.getUserById(savedDocument.getCreatedBy()), userService.getUserById(savedDocument.getUpdatedBy()));
                newDocuments.add(newDocument);
            }
            return newDocuments;
        }catch (Exception e){
            throw new ApiException("Unable to save documents");
        }

    }



    @Override
    public IDocument updateDocument(String documentId, String name, String description) {
        return null;
    }

    @Override
    public void deleteDocument(String documentId) {

    }

    @Override
    public IDocument getDocumentByDocumentId(String documentId) {
        return null;
    }

    @Override
    public Resource getResource(String documentName) {
        return null;
    }
}

