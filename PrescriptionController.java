package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
// Assume a PrescriptionService and TokenService exist to handle business logic
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing prescriptions.
 */
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;
    
    @Autowired
    private TokenService tokenService; // Service for token validation

    /**
     * Creates a new prescription.
     *
     * @param prescription The prescription to be created.
     * @return The created prescription with a 201 Created status.
     */
    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@Valid @RequestBody Prescription prescription) {
        try {
            Prescription newPrescription = prescriptionService.createPrescription(prescription);
            return new ResponseEntity<>(newPrescription, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This could happen if the patient, doctor, or appointment ID is invalid
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets a specific prescription by its ID. Requires a valid token.
     *
     * @param id The ID of the prescription.
     * @param token The authorization token.
     * @return The prescription if found, otherwise a 404 Not Found or 401 Unauthorized response.
     */
    @GetMapping("/{id}/{token}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable Long id, @PathVariable String token) {
        if (!tokenService.isTokenValid(token)) { // Simplified token validation
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Prescription> prescription = prescriptionService.findPrescriptionById(id);
        return prescription.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Gets all prescriptions for a specific patient. Requires a valid token.
     *
     * @param patientId The ID of the patient.
     * @param token The authorization token.
     * @return A list of prescriptions for the patient.
     */
    @GetMapping("/patient/{patientId}/{token}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByPatientId(@PathVariable Long patientId, @PathVariable String token) {
        if (!tokenService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Prescription> prescriptions = prescriptionService.findPrescriptionsByPatientId(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    /**
     * Gets all prescriptions written by a specific doctor. Requires a valid token.
     *
     * @param doctorId The ID of the doctor.
     * @param token The authorization token.
     * @return A list of prescriptions written by the doctor.
     */
    @GetMapping("/doctor/{doctorId}/{token}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByDoctorId(@PathVariable Long doctorId, @PathVariable String token) {
        if (!tokenService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Prescription> prescriptions = prescriptionService.findPrescriptionsByDoctorId(doctorId);
        return ResponseEntity.ok(prescriptions);
    }
}

