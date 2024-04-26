package com.clearsolutions.usermanager.service.impl;

import com.clearsolutions.usermanager.exceptions.custom.EntityAlreadyExistsException;
import com.clearsolutions.usermanager.exceptions.custom.EntityNotFoundException;
import com.clearsolutions.usermanager.model.User;
import com.clearsolutions.usermanager.repository.UserRepository;
import com.clearsolutions.usermanager.service.UserService;
import com.clearsolutions.usermanager.testutils.FakeDataGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testGetExistingUserById_ShouldNotThrowAnyException() {
        // Prepare
        long id = 1L;
        var user = FakeDataGenerator.userBuilder().build();

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));

        // Execute & Verify
        assertDoesNotThrow(() -> userService.getById(id));
    }

    @Test
    void testGetNotExistingUserById_ShouldThrowEntityNotFoundException() {
        // Prepare
        long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows("User with ID: 1 was not found!",
                EntityNotFoundException.class, () -> userService.getById(id));
    }

    @Test
    void testFindUsersByBirthDateRange_ShouldReturnListOfUsers() {
        // Prepare
        var from = LocalDate.of(1990, 1, 1);
        var to = LocalDate.of(2000, 1, 1);
        var expectedUsers = FakeDataGenerator.getUsers();

        when(userRepository.findUserByBirthDateBetween(from, to)).thenReturn(expectedUsers);

        // Act
        var actualUsers = userService.findUsersByBirthDateRange(from, to);

        // Assert
        assertEquals(expectedUsers.size(), actualUsers.size());
    }

    @Test
    void testCreateNotExistingUser_ShouldReturnSavedPublication() {
        // Prepare
        var user = FakeDataGenerator.userBuilder().build();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        // Execute & Verify
        assertDoesNotThrow(() -> userService.create(user));
    }

    @Test
    void testCreateAlreadyExistingUser_ShouldThrowEntityAlreadyExistsException() {
        // Prepare
        var user = FakeDataGenerator.userBuilder().build();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Execute & Verify
        assertThrows("User with email: " + user.getEmail() + " already exists!",
                EntityAlreadyExistsException.class, () -> userService.create(user));
    }

    @Test
    void testUpdateExistingUser_ShouldNotThrowAnyException() {
        // Prepare
        long userId = 1L;
        var user = FakeDataGenerator.userBuilder().build();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Execute & Verify
        assertDoesNotThrow(() -> userService.update(userId, user));
    }

    @Test
    void testUpdateNotExistingUser_ShouldThrowEntityNotFoundException() {
        // Prepare
        long userId = 1L;
        var user = FakeDataGenerator.userBuilder().build();
        user.setId(userId);

        when(userRepository.existsById(userId)).thenReturn(false);

        // Execute & Verify
        assertThrows("User with ID: 1 was not found!", EntityNotFoundException.class,
                () -> userService.update(userId, user));
    }

    @Test
    void testDeleteExistingUserById_ShouldNotThrowAnyException() {
        // Prepare
        long id = 1L;

        when(userRepository.existsById(id)).thenReturn(true);

        // Execute & Verify
        assertDoesNotThrow(() -> userService.deleteById(id));
    }

    @Test
    void testDeleteNotExistingUserById_ShouldThrowEntityNotFoundException() {
        // Prepare
        long id = 1L;

        when(userRepository.existsById(id)).thenReturn(false);

        // Execute & Verify
        assertThrows("User with ID: 1 was not found!",
                EntityNotFoundException.class, () -> userService.deleteById(id));
    }

    @Test
    void testUpdateFirstNameOfExistingUser_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newFirstName = "John";
        var existingUser = FakeDataGenerator.userBuilder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        var updatedUser = userService.updateFirstName(userId, newFirstName);

        // Assert
        assertEquals(newFirstName, updatedUser.getFirstName());
    }

    @Test
    void testUpdateFirstNameOfNotExistingUser_ShouldThrowEntityNotFoundException() {
        // Prepare
        long userId = 1L;
        var newFirstName = "John";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows("User with ID: 1 was not found!",
                EntityNotFoundException.class, () -> userService.updateFirstName(userId, newFirstName));
    }

    @Test
    void testUpdateLastNameOfExistingUser_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newLastName = "Doe";
        var existingUser = FakeDataGenerator.userBuilder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        var updatedUser = userService.updateLastName(userId, newLastName);

        // Assert
        assertEquals(newLastName, updatedUser.getLastName());
    }

    @Test
    void testUpdateLastNameOfNotExistingUser_ShouldThrowEntityNotFoundException() {
        // Prepare
        long userId = 1L;
        var newLastName = "Doe";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows("User with ID: 1 was not found!",
                EntityNotFoundException.class, () -> userService.updateLastName(userId, newLastName));
    }

    @Test
    void testUpdatePhoneOfExistingUser_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newPhone = "111-222-333";
        var existingUser = FakeDataGenerator.userBuilder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        var updatedUser = userService.updatePhone(userId, newPhone);

        // Assert
        assertEquals(newPhone, updatedUser.getPhone());
    }

    @Test
    void testUpdatePhoneOfNotExistingUser_ShouldThrowEntityNotFoundException() {
        // Prepare
        long userId = 1L;
        var newPhone = "111-222-333";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows("User with ID: 1 was not found!",
                EntityNotFoundException.class, () -> userService.updatePhone(userId, newPhone));
    }

    @Test
    void testUpdateAddressOfExistingUser_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newAddress = "Liberty Street";
        var existingUser = FakeDataGenerator.userBuilder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        var updatedUser = userService.updateAddress(userId, newAddress);

        // Assert
        assertEquals(newAddress, updatedUser.getAddress());
    }

    @Test
    void testUpdateAddressOfNotExistingUser_ShouldThrowEntityNotFoundException() {
        // Prepare
        long userId = 1L;
        var newAddress = "Liberty Street";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows("User with ID: 1 was not found!",
                EntityNotFoundException.class, () -> userService.updateAddress(userId, newAddress));
    }

    @Test
    void testUpdateBirthdateOfExistingUser_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newBirthDate = LocalDate.of(2000, 1, 1);
        var existingUser = FakeDataGenerator.userBuilder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        var updatedUser = userService.updateBirthdate(userId, newBirthDate);

        // Assert
        assertEquals(newBirthDate, updatedUser.getBirthDate());
    }

    @Test
    void testUpdateBirthdateOfNotExistingUser_ShouldThrowEntityNotFoundException() {
        // Prepare
        long userId = 1L;
        var newBirthDate = LocalDate.of(2000, 1, 1);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows("User with ID: 1 was not found!",
                EntityNotFoundException.class, () -> userService.updateBirthdate(userId, newBirthDate));
    }

    @Test
    void testUpdateEmailOfExistingUser_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newEmail = "john@example.com";
        var existingUser = FakeDataGenerator.userBuilder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        var updatedUser = userService.updateEmail(userId, newEmail);

        // Assert
        assertEquals(newEmail, updatedUser.getEmail());
    }

    @Test
    void testUpdateEmailOfNotExistingUser_ShouldThrowEntityNotFoundException() {
        // Prepare
        long userId = 1L;
        var newEmail = "john@example.com";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows("User with ID: 1 was not found!",
                EntityNotFoundException.class, () -> userService.updateEmail(userId, newEmail));
    }

    @Test
    void testUpdateEmailOfExistingUser_WhenEmailIsNotUnique_ShouldThrowEntityAlreadyExistsException() {
        // Prepare
        long userId = 1L;
        var newEmail = "john@example.com";
        var existingUser = FakeDataGenerator.userBuilder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(newEmail))
                .thenThrow(new EntityAlreadyExistsException(User.class.getSimpleName(), "Email: " + newEmail));

        // Execute & Verify
        assertThrows("User with Email: " + newEmail + " 1 was not found!",
                EntityAlreadyExistsException.class, () -> userService.updateEmail(userId, newEmail));
    }
}
