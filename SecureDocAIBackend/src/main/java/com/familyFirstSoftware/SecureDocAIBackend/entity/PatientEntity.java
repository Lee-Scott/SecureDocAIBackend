package com.familyFirstSoftware.SecureDocAIBackend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients")
@JsonInclude(NON_DEFAULT)
public class PatientEntity extends Auditable {

    @Column(unique = true, nullable = false, updatable = false)
    private String patientId;

    private String firstName;
    private String lastName;
    private LocalDate dob;
    
    @Column(unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "patient")
    @ToString.Exclude
    private List<DocumentEntity> documents;
}
