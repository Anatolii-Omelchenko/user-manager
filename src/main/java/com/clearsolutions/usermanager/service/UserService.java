package com.clearsolutions.usermanager.service;

import com.clearsolutions.usermanager.model.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    User getById(Long id);

    List<User> findUsersByBirthDateRange(LocalDate from, LocalDate to);

    User create(User user);

    User update(Long id, User user);

    User updateFirstName(Long id, String firstName);

    User updateLastName(Long id, String lastName);

    User updateEmail(Long id, String email);

    User updateBirthdate(Long id, LocalDate birthday);

    User updateAddress(Long id, String address);

    User updatePhone(Long id, String phone);

    void deleteById(Long id);
}
