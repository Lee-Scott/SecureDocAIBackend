package com.familyFirstSoftware.SecureDocAIBackend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;



@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documents")
@JsonInclude(NON_DEFAULT)
public class DocumentEntity extends Auditable {
    @Column(updatable = false, unique = true, nullable = false)
    private String documentId;
    private String name;
    private String description;
    private String uri;
    private long size;
    private String formattedSize;
    private String icon;
    private String extension;
    private String mimeType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_documents_owner")
            //foreignKey = @ForeignKey(name = "fk_documents_owner", foreignKeyDefinition = "foreign key /* FK */ (user_id) references UserEntity", value = ConstraintMode.CONSTRAINT)
    )
    private UserEntity owner;
    
    /**
     * Gets the full file path for this document
     * @return The full file path
     */
    public String getFilePath() {
        return this.uri != null ? this.uri : "";
    }
    
    /**
     * Gets the MIME type of the document
     * @return The MIME type
     */
    public String getMimeType() {
        return this.mimeType != null ? this.mimeType : "application/octet-stream";
    }
}