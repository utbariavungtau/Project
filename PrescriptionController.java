package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
// Assume a PrescriptionService exists to handle business logic
import com.project.back_end.services.PrescriptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Creates a new prescription.
     *
     * @param prescription The prescription to be created.
     * @return The created prescription with a 201 Created status.
     */
    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription) {
        try {
            Prescription newPrescription = prescriptionService.createPrescription(prescription);
            return new ResponseEntity<>(newPrescription, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This could happen if the patient, doctor, or appointment ID is invalid
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets a specific prescription by its ID.
     *
     * @param id The ID of the prescription.
     * @return The prescription if found, otherwise a 404 Not Found response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable Long id) {
        Optional<Prescription> prescription = prescriptionService.findPrescriptionById(id);
        return prescription.map(ResponseEntity::ok)
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Gets all prescriptions for a specific patient.
     *
     * @param patientId The ID of the patient.
     * @return A list of prescriptions for the patient.
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByPatientId(@PathVariable Long patientId) {
        List<Prescription> prescriptions = prescriptionService.findPrescriptionsByPatientId(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    /**
     * Gets all prescriptions written by a specific doctor.
     *
     * @param doctorId The ID of the doctor.
     * @return A list of prescriptions written by the doctor.
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByDoctorId(@PathVariable Long doctorId) {
        List<Prescription> prescriptions = prescriptionService.findPrescriptionsByDoctorId(doctorId);
        return ResponseEntity.ok(prescriptions);
    }
}
