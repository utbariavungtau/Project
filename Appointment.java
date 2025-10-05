package com.project.back_end.models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;

/**
 * Represents an Appointment in the Smart Clinic Management System.
 * This class is mapped to the "appointments" table in the database.
 */
@Entity
@Table(name = "appointments")
public class Appointment {

    public enum AppointmentStatus {
        SCHEDULED,
        COMPLETED,
        CANCELED,
        REQUESTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_datetime", nullable = false)
    private LocalDateTime appointmentDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.REQUESTED;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    // Default constructor
    public Appointment() {
    }

    // Parameterized constructor
    public Appointment(Patient patient, Doctor doctor, LocalDateTime appointmentDatetime, String reason) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDatetime = appointmentDatetime;
        this.reason = reason;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getAppointmentDatetime() {
        return appointmentDatetime;
    }

    public void setAppointmentDatetime(LocalDateTime appointmentDatetime) {
        this.appointmentDatetime = appointmentDatetime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patientId=" + (patient != null ? patient.getId() : "null") +
                ", doctorId=" + (doctor != null ? doctor.getId() : "null") +
                ", appointmentDatetime=" + appointmentDatetime +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

