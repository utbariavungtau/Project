package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Appointment.AppointmentStatus;
// Assuming a repository layer exists for data access
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing appointment-related business logic.
 */
@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository; // To validate doctor existence

    @Autowired
    private PatientRepository patientRepository; // To validate patient existence

    /**
     * Requests a new appointment.
     * The initial status will be set to REQUESTED.
     *
     * @param appointment The appointment object to be created.
     * @return The saved appointment object.
     * @throws IllegalArgumentException if the patient or doctor does not exist,
     * or if the appointment time is in the past.
     */
    public Appointment requestAppointment(Appointment appointment) {
        // Validate that patient and doctor exist
        if (!patientRepository.existsById(appointment.getPatientId())) {
            throw new IllegalArgumentException("Patient not found with ID: " + appointment.getPatientId());
        }
        if (!doctorRepository.existsById(appointment.getDoctorId())) {
            throw new IllegalArgumentException("Doctor not found with ID: " + appointment.getDoctorId());
        }
        // Validate appointment time is in the future
        if (appointment.getAppointmentDatetime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date and time must be in the future.");
        }

        appointment.setStatus(AppointmentStatus.REQUESTED);
        return appointmentRepository.save(appointment);
    }

    /**
     * Confirms an appointment by changing its status to SCHEDULED.
     *
     * @param appointmentId The ID of the appointment to confirm.
     * @return The updated appointment, or empty if not found.
     */
    public Optional<Appointment> confirmAppointment(Long appointmentId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            return Optional.of(appointmentRepository.save(appointment));
        }
        return Optional.empty();
    }

    /**
     * Cancels an appointment by changing its status to CANCELED.
     *
     * @param appointmentId The ID of the appointment to cancel.
     * @return The updated appointment, or empty if not found.
     */
    public Optional<Appointment> cancelAppointment(Long appointmentId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(AppointmentStatus.CANCELED);
            return Optional.of(appointmentRepository.save(appointment));
        }
        return Optional.empty();
    }

    /**
     * Finds an appointment by its ID.
     * @param id The ID of the appointment.
     * @return An Optional containing the appointment if found.
     */
    public Optional<Appointment> findAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    /**
     * Finds all appointments for a given patient.
     * @param patientId The ID of the patient.
     * @return A list of appointments.
     */
    public List<Appointment> findAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    /**
     * Finds all appointments for a given doctor.
     * @param doctorId The ID of the doctor.
     * @return A list of appointments.
     */
    public List<Appointment> findAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
}
