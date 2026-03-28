package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.domain.Response;
import com.familyFirstSoftware.SecureDocAIBackend.dto.response.HttpResponse;
import com.familyFirstSoftware.SecureDocAIBackend.dto.Patient;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.CreatePatientRequest;
import com.familyFirstSoftware.SecureDocAIBackend.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.familyFirstSoftware.SecureDocAIBackend.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;

@Slf4j
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientResource {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<Response> createPatient(@RequestBody CreatePatientRequest request, HttpServletRequest httpRequest) {
        patientService.createPatient(request);
        return ResponseEntity.created(URI.create("")).body(getResponse(httpRequest, emptyMap(), "Patient created successfully.", HttpStatus.CREATED));
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<Response> getPatientById(@PathVariable("patientId") String patientId, HttpServletRequest httpRequest) {
        Patient patient = patientService.getPatientById(patientId);
        return ResponseEntity.ok().body(getResponse(httpRequest, Map.of("patient", patient), "Patient retrieved successfully.", HttpStatus.OK));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Response> getPatientByEmail(@PathVariable("email") String email, HttpServletRequest httpRequest) {
        Patient patient = patientService.getPatientByEmail(email);
        return ResponseEntity.ok().body(getResponse(httpRequest, Map.of("patient", patient), "Patient retrieved successfully.", HttpStatus.OK));
    }

    @GetMapping
    public ResponseEntity<HttpResponse> getPatients() {
        return ResponseEntity.ok(
            HttpResponse.ok(patientService.getPatients(), "Patients retrieved")
        );
    }
}
