package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing doctor-related business logic.
 */
@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    /**
     * Retrieves all doctors from the database.
     *
     * @return A list of all doctors.
     */
    public List<Doctor> findAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Finds a doctor by their unique ID.
     *
     * @param id The ID of the doctor to find.
     * @return An Optional containing the doctor if found, otherwise empty.
     */
    public Optional<Doctor> findDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    /**
     * Finds all doctors who match a given specialty.
     *
     * @param specialty The specialty to search for.
     * @return A list of doctors with the specified specialty.
     */
    public List<Doctor> findDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    /**
     * Retrieves all appointments for a specific doctor.
     *
     * @param doctorId The ID of the doctor.
     * @return A list of appointments for the given doctor.
     */
    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
}
