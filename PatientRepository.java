package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Patient entity.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Finds a patient by their email address.
     * @param email The email address to search for.
     * @return An Optional containing the patient if found.
     */
    Optional<Patient> findByEmail(String email);

    /**
     * Finds patients whose first name or last name contains the given search term, ignoring case.
     * This is useful for implementing a patient search feature.
     * @param firstName The search term for the first name.
     * @param lastName The search term for the last name.
     * @return A list of patients matching the criteria.
     */
    List<Patient> findByFirstNameContainingOrLastNameContainingAllIgnoreCase(String firstName, String lastName);
}

