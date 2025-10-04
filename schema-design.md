MySQL Database Schema for Smart Clinic Management System
This document outlines the MySQL database schema designed to support the features defined in the user stories for the Smart Clinic Management System.

Schema Diagram
+-------------+      +-------------+      +--------------------+
|    Users    |<--+--|   Patients  |      | DoctorAvailability |
|-------------|   |  |-------------|      |--------------------|
| id (PK)     |   |  | id (PK)     |      | id (PK)            |
| email       |   |  | user_id (FK)|      | doctor_id (FK)     |
| password_hash|  |  | first_name  |      | day_of_week        |
| role        |   |  | last_name   |      | start_time         |
| created_at  |   |  | ...         |      | end_time           |
+-------------+   |  +-------------+      +--------------------+
                  |         |
                  |         |
                  |  +-------------+      +--------------------+
                  +--|   Doctors   |----->|      Reviews       |
                     |-------------|      |--------------------|
                     | id (PK)     |      | id (PK)            |
                     | user_id (FK)|      | patient_id (FK)    |
                     | first_name  |      | doctor_id (FK)     |
                     | last_name   |      | rating             |
                     | specialty   |      | comment            |
                     | ...         |      | created_at         |
                     +-------------+      +--------------------+
                            |
                            |
           +----------------+----------------+
           |                                 |
+--------------------+              +--------------------+
|    Appointments    |              |   MedicalRecords   |
|--------------------|              |--------------------|
| id (PK)            |              | id (PK)            |
| patient_id (FK)    |------------->| appointment_id (FK)|
| doctor_id (FK)     |              | patient_id (FK)    |
| appointment_datetime|             | doctor_id (FK)     |
| status             |              | diagnosis          |
| ...                |              | notes              |
+--------------------+              +--------------------+
           |                                 |
           |                  +--------------+--------------+
+--------------------+        |                             |
|      Invoices      |  +---------------+         +--------------------+
|--------------------|  | Prescriptions |         |      LabTests      |
| id (PK)            |  |---------------|         |--------------------|
| appointment_id (FK)|  | id (PK)       |         | id (PK)            |
| patient_id (FK)    |  | med_rec_id(FK)|         | med_rec_id(FK)     |
| amount             |  | ...           |         | ...                |
| status             |  +---------------+         +--------------------+
| ...                |
+--------------------+

SQL CREATE TABLE Statements
Here are the SQL statements to create the tables for the smart clinic system.

1. users Table
Stores login and role information for all system users (Patients, Doctors, Admins).

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('PATIENT', 'DOCTOR', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

2. patients Table
Stores detailed profile information for patients.

CREATE TABLE patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    phone_number VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

3. doctors Table
Stores detailed profile information for doctors.

CREATE TABLE doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    specialty VARCHAR(150),
    qualifications TEXT,
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

4. doctor_availability Table
Stores the weekly availability schedule for doctors.

CREATE TABLE doctor_availability (
    id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    UNIQUE(doctor_id, day_of_week, start_time, end_time),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

5. appointments Table
Stores information about scheduled appointments.

CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_datetime DATETIME NOT NULL,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELED', 'REQUESTED') NOT NULL DEFAULT 'REQUESTED',
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

6. medical_records Table
Stores notes and diagnoses from consultations.

CREATE TABLE medical_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL UNIQUE,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    diagnosis TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

7. prescriptions Table
Stores prescription details linked to a medical record.

CREATE TABLE prescriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medical_record_id INT NOT NULL,
    medication_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100),
    instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id) ON DELETE CASCADE
);

8. lab_tests Table
Stores information about ordered lab tests and their results.

CREATE TABLE lab_tests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medical_record_id INT NOT NULL,
    test_name VARCHAR(255) NOT NULL,
    result TEXT,
    status ENUM('ORDERED', 'COMPLETED') DEFAULT 'ORDERED',
    ordered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id) ON DELETE CASCADE
);

9. invoices Table
Stores billing information for appointments.

CREATE TABLE invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    patient_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    status ENUM('PAID', 'UNPAID') DEFAULT 'UNPAID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);

10. reviews Table
Stores patient reviews and ratings for doctors.

CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);
