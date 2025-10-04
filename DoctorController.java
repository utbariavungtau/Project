package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.DoctorAvailability;
// Assume a DoctorService exists to handle business logic
import com.project.back_end.services.DoctorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * Get the availability schedule for a specific doctor.
     * @param id The ID of the doctor.
     * @return A list of availability slots.
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<List<DoctorAvailability>> getDoctorAvailability(@PathVariable Long id) {
        if (!doctorService.findDoctorById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<DoctorAvailability> availability = doctorService.getDoctorAvailability(id);
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
