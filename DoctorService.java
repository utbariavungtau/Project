package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.User; // Assuming a User entity for login
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.UserRepository; // Assuming a UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // For password validation
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing doctor-related business logic.
 */
@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private UserRepository userRepository; // For login validation

    @Autowired
    private PasswordEncoder passwordEncoder; // For secure password checking

    /**
     * Retrieves all doctors from the database.
     * @return A list of all doctors.
     */
    public List<Doctor> findAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Finds a doctor by their unique ID.
     * @param id The ID of the doctor to find.
     * @return An Optional containing the doctor if found, otherwise empty.
     */
    public Optional<Doctor> findDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    /**
     * Finds all doctors who match a given specialty.
     * @param specialty The specialty to search for.
     * @return A list of doctors with the specified specialty.
     */
    public List<Doctor> findDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    /**
     * Retrieves all appointments for a specific doctor.
     * @param doctorId The ID of the doctor.
     * @return A list of appointments for the given doctor.
     */
    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    /**
     * Validates a doctor's login credentials.
     * @param email The doctor's email address.
     * @param password The doctor's password.
     * @return An Optional containing the Doctor if credentials are valid, otherwise empty.
     */
    public Optional<Doctor> validateLogin(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Securely compare the provided password with the stored hashed password
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Check if the user has the 'DOCTOR' role
                if ("DOCTOR".equals(user.getRole())) {
                    return doctorRepository.findByUserId(user.getId());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves the available time slots for a doctor on a specific date.
     * It filters out the times that are already booked.
     * @param doctorId The ID of the doctor.
     * @param date The date to check for availability.
     * @return A list of available time slots as strings (e.g., "09:00", "10:00").
     */
    public List<String> getAvailableTimeSlots(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return List.of(); // Return empty list if doctor not found
        }

        Doctor doctor = doctorOpt.get();
        List<String> allPossibleSlots = doctor.getAvailableTimes();

        // Get appointments for the doctor on the specified date
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentDatetimeBetween(doctorId, startOfDay, endOfDay);

        // Extract the time part of booked appointments (e.g., "09:30")
        List<String> bookedSlots = bookedAppointments.stream()
            .map(appointment -> appointment.getAppointmentDatetime().toLocalTime().toString().substring(0, 5))
            .collect(Collectors.toList());

        // Return only the slots that are not already booked
        return allPossibleSlots.stream()
            .filter(slot -> !bookedSlots.contains(slot))
            .collect(Collectors.toList());
    }
}

