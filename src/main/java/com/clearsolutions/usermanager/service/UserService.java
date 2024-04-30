package com.clearsolutions.usermanager.service;

import com.clearsolutions.usermanager.exceptions.custom.EntityAlreadyExistsException;
import com.clearsolutions.usermanager.exceptions.custom.EntityNotFoundException;
import com.clearsolutions.usermanager.dto.DateRange;
import com.clearsolutions.usermanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Interface defining the service operations for managing users.
 */
public interface UserService {

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id The unique identifier of the user.
     * @return The user with the specified ID.
     * @throws EntityNotFoundException if no user with the specified ID is found.
     */
    User getById(Long id) throws EntityNotFoundException;

    /**
     * Retrieves a page of users whose birth dates fall within the specified range.
     *
     * @param dateRange The {@link DateRange} representing the range of birth dates to filter by.
     * @param pageable  The pagination information for the query.
     * @return A {@link Page} of users whose birth dates fall within the specified range.
     */
    Page<User> findUsersByBirthDateRange(DateRange dateRange, Pageable pageable);

    /**
     * Creates a new user.
     *
     * @param user The user to create.
     * @return The created user.
     * @throws EntityAlreadyExistsException if a user with the same email already exists.
     */
    User create(User user) throws EntityAlreadyExistsException;

    /**
     * Updates an existing user.
     *
     * @param id   The unique identifier of the user to update.
     * @param user The updated user data.
     * @return The updated user.
     * @throws EntityNotFoundException    if no user with the specified ID is found.
     * @throws EntityAlreadyExistsException if a user with the same email already exists.
     */
    User update(Long id, User user) throws EntityNotFoundException, EntityAlreadyExistsException;

    /**
     * Updates the first name of an existing user.
     *
     * @param id        The unique identifier of the user.
     * @param firstName The new first name.
     * @return The updated user.
     * @throws EntityNotFoundException    if no user with the specified ID is found.
     */
    User updateFirstName(Long id, String firstName) throws EntityNotFoundException;

    /**
     * Updates the last name of an existing user.
     *
     * @param id       The unique identifier of the user.
     * @param lastName The new last name.
     * @return The updated user.
     * @throws EntityNotFoundException if no user with the specified ID is found.
     */
    User updateLastName(Long id, String lastName) throws EntityNotFoundException;

    /**
     * Updates the email address of an existing user.
     *
     * @param id    The unique identifier of the user.
     * @param email The new email address.
     * @return The updated user.
     * @throws EntityNotFoundException    if no user with the specified ID is found.
     * @throws EntityAlreadyExistsException if a user with the same email already exists.
     */
    User updateEmail(Long id, String email) throws EntityNotFoundException, EntityAlreadyExistsException;

    /**
     * Updates the birthdate of an existing user.
     *
     * @param id       The unique identifier of the user.
     * @param birthday The new birthdate.
     * @return The updated user.
     * @throws EntityNotFoundException if no user with the specified ID is found.
     */
    User updateBirthdate(Long id, LocalDate birthday) throws EntityNotFoundException;

    /**
     * Updates the address of an existing user.
     *
     * @param id      The unique identifier of the user.
     * @param address The new address.
     * @return The updated user.
     * @throws EntityNotFoundException if no user with the specified ID is found.
     */
    User updateAddress(Long id, String address) throws EntityNotFoundException;

    /**
     * Updates the phone number of an existing user.
     *
     * @param id    The unique identifier of the user.
     * @param phone The new phone number.
     * @return The updated user.
     * @throws EntityNotFoundException if no user with the specified ID is found.
     */
    User updatePhone(Long id, String phone) throws EntityNotFoundException;

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id The unique identifier of the user to delete.
     * @throws EntityNotFoundException if no user with the specified ID is found.
     */
    void deleteById(Long id) throws EntityNotFoundException;
}
