package com.clearsolutions.usermanager.service.impl;

import com.clearsolutions.usermanager.exceptions.custom.EntityAlreadyExistsException;
import com.clearsolutions.usermanager.exceptions.custom.EntityNotFoundException;
import com.clearsolutions.usermanager.dto.DateRange;
import com.clearsolutions.usermanager.model.User;
import com.clearsolutions.usermanager.repository.UserRepository;
import com.clearsolutions.usermanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Implementation of the service for managing user-related operations.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), "ID: " + id));
    }

    @Override
    public Page<User> findUsersByBirthDateRange(DateRange dateRange, Pageable pageable) {
        return userRepository.findUserByBirthDateBetween(dateRange.from(), dateRange.to(), pageable);
    }

    @Override
    @Transactional
    public User create(User user) {
        var existByEmail = userRepository.existsByEmail(user.getEmail());
        if (existByEmail) {
            throw new EntityAlreadyExistsException(User.class.getSimpleName(), "Email: " + user.getEmail());
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Long id, User user) {
        var userForUpdate = getById(id);
        validateUniqueEmail(userForUpdate, user.getEmail());

        userForUpdate.setFirstName(user.getFirstName());
        userForUpdate.setLastName(user.getLastName());
        userForUpdate.setEmail(user.getEmail());
        userForUpdate.setBirthDate(user.getBirthDate());
        userForUpdate.setAddress(user.getAddress());
        userForUpdate.setPhone(user.getPhone());

        return userForUpdate;
    }

    @Override
    @Transactional
    public User updateFirstName(Long id, String firstName) {
        var userForUpdate = getById(id);
        userForUpdate.setFirstName(firstName);

        return userForUpdate;
    }

    @Override
    @Transactional
    public User updateLastName(Long id, String lastName) {
        var userForUpdate = getById(id);
        userForUpdate.setLastName(lastName);

        return userForUpdate;
    }

    @Override
    @Transactional
    public User updateEmail(Long id, String email) {
        var userForUpdate = getById(id);
        validateUniqueEmail(userForUpdate, email);
        userForUpdate.setEmail(email);

        return userForUpdate;
    }

    @Override
    @Transactional
    public User updateBirthdate(Long id, LocalDate birthday) {
        var userForUpdate = getById(id);
        userForUpdate.setBirthDate(birthday);

        return userForUpdate;
    }

    @Override
    @Transactional
    public User updateAddress(Long id, String address) {
        var userForUpdate = getById(id);
        userForUpdate.setAddress(address);

        return userForUpdate;
    }

    @Override
    @Transactional
    public User updatePhone(Long id, String phone) {
        var userForUpdate = getById(id);
        userForUpdate.setPhone(phone);

        return userForUpdate;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(User.class.getSimpleName(), "ID: " + id);
        }
        userRepository.deleteById(id);
    }

    private void validateUniqueEmail(User existingUser, String newEmail) {
        if (userRepository.existsByEmail(newEmail) && !newEmail.equals(existingUser.getEmail())) {
            throw new EntityAlreadyExistsException(User.class.getSimpleName(), "Email already exists: " + newEmail);
        }
    }

}
