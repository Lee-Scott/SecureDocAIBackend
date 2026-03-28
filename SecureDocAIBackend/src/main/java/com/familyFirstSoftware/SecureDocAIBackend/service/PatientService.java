package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.dto.Patient;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.CreatePatientRequest;

import java.util.List;

public interface PatientService {
    void createPatient(CreatePatientRequest request);
    Patient getPatientByEmail(String email);
    Patient getPatientById(String patientId);
    List<Patient> getPatients();
}