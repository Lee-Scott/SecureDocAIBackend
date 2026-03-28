package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.dto.Patient;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.CreatePatientRequest;
import com.familyFirstSoftware.SecureDocAIBackend.entity.PatientEntity;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.PatientRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.PatientService;
import com.familyFirstSoftware.SecureDocAIBackend.utils.DtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public void createPatient(CreatePatientRequest request) {
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Patient with this email already exists.");
        }

        PatientEntity entity = new PatientEntity();
        entity.setPatientId(UUID.randomUUID().toString());
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setEmail(request.getEmail());
        entity.setDob(request.getDob());

        patientRepository.save(entity);
    }

    @Override
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .map(DtoMapper::fromPatientEntity)
                .orElseThrow(() -> new ApiException("Patient not found with email: " + email));
    }

    @Override
    public Patient getPatientById(String patientId) {
        return patientRepository.findByPatientId(patientId)
                .map(DtoMapper::fromPatientEntity)
                .orElseThrow(() -> new ApiException("Patient not found with ID: " + patientId));
    }

    @Override
    public List<Patient> getPatients() {
        return patientRepository.findAll().stream()
                .map(DtoMapper::fromPatientEntity)
                .collect(Collectors.toList());
    }
}