package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.DoctorAvailability;
// Assume a DoctorService exists to handle business logic
import com.project.back_end.services.DoctorService;
// Assume a TokenService exists for validation
import com.project.back_end.services.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing doctor-related operations.
 */
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService; // Service to interact with doctor data
    
    @Autowired
    private TokenService tokenService; // Service for token validation

    /**
     * Get a list of all doctors.
     * @return A list of all doctors.
     */
    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.findAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    /**
     * Get a specific doctor by their ID.
     * @param id The ID of the doctor.
     * @return The doctor's information if found, otherwise a 404 Not Found response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        Optional<Doctor> doctor = doctorService.findDoctorById(id);
        return doctor.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all appointments for a specific doctor.
     * @param id The ID of the doctor.
     * @return A list of appointments for the specified doctor.
     */
    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(@PathVariable Long id) {
        // First, check if the doctor exists
        if (!doctorService.findDoctorById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<Appointment> appointments = doctorService.findAppointmentsByDoctorId(id);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get the availability schedule for a specific doctor on a given date.
     * This endpoint is secured and requires a valid token following the specified URL pattern.
     * @param user The user requesting the information.
     * @param doctorId The ID of the doctor.
     * @param date The date to check for availability (YYYY-MM-DD).
     * @param token The authorization token.
     * @return A list of availability slots for the specified doctor and date.
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<List<DoctorAvailability>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String token) {
        
        // Step 1: Validate the token.
        // In a real application, this service would handle complex validation logic.
        if (!tokenService.isTokenValid(token, user)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Step 2: Check if the doctor exists.
        if (!doctorService.findDoctorById(doctorId).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Step 3: Retrieve availability for the specific doctor and date.
        // Assuming the service layer can filter availability by date.
        List<DoctorAvailability> availability = doctorService.findAvailabilityByDoctorIdAndDate(doctorId, date);
        return ResponseEntity.ok(availability);
    }

    /**
     * Add a new availability slot for a doctor.
     * @param id The ID of the doctor.
     * @param newAvailability The new availability slot to add.
     * @return The created availability slot.
     */
    @PostMapping("/{id}/availability")
    public ResponseEntity<DoctorAvailability> addDoctorAvailability(@PathVariable Long id, @RequestBody DoctorAvailability newAvailability) {
         if (!doctorService.findDoctorById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        newAvailability.setDoctorId(id); // Ensure the availability is linked to the correct doctor
        DoctorAvailability savedAvailability = doctorService.saveAvailability(newAvailability);
        return new ResponseEntity<>(savedAvailability, HttpStatus.CREATED);
    }
}

