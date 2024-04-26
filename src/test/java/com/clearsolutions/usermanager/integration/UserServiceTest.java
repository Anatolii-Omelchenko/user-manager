package com.clearsolutions.usermanager.integration;

import com.clearsolutions.usermanager.exceptions.custom.EntityAlreadyExistsException;
import com.clearsolutions.usermanager.exceptions.custom.EntityNotFoundException;
import com.clearsolutions.usermanager.service.UserService;
import com.clearsolutions.usermanager.testutils.FakeDataGenerator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = NONE)
@Testcontainers
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest {

    @Autowired
    private UserService userService;

    private static final int TOTAL_USERS = 10;

    @Test
    void testFindUsersByBirthDateRange() {
        // Prepare
        var fromDate = LocalDate.of(1900, 1, 1);
        var toDate = LocalDate.of(2024, 1, 1);

        // Execute
        var users = userService.findUsersByBirthDateRange(fromDate, toDate);

        // Assert
        assertEquals(users.size(), TOTAL_USERS);
    }

    @Test
    void testFindById() {
        // Prepare
        long userId = 1L;

        // Execute
        var user = userService.getById(userId);

        // Assert
        assertNotNull(user);
        assertEquals(user.getId(), userId);
    }

    @Test
    void testFindById_WhenUserNotFound_ShouldThrowException() {
        // Prepare
        long userId = 1_000_000L;

        // Execute & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    void testCreate() {
        // Prepare
        var user = FakeDataGenerator.userBuilder().build();

        // Execute
        var createdUser = userService.create(user);

        // Assert
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(createdUser.getFirstName(), user.getFirstName());
        assertEquals(createdUser.getLastName(), user.getLastName());
        assertEquals(createdUser.getEmail(), user.getEmail());
        assertEquals(createdUser.getBirthDate(), user.getBirthDate());
        assertEquals(createdUser.getPhone(), user.getPhone());
        assertEquals(createdUser.getAddress(), user.getAddress());
    }

    @Test
    void testCreate_WhenEmailAlreadyExists_ShouldThrowException() {
        // Prepare
        var existingUser = userService.getById(1L);
        var user = FakeDataGenerator.userBuilder()
                .email(existingUser.getEmail())
                .build();

        // Execute & Assert
        assertThrows(EntityAlreadyExistsException.class, () -> userService.create(user));
    }

    @Test
    void testUpdate() {
        // Prepare
        long userId = 1L;
        var user = FakeDataGenerator.userBuilder().build();

        // Execute
        var updatedUser = userService.update(userId, user);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(updatedUser.getId(), userId);
        assertEquals(updatedUser.getFirstName(), user.getFirstName());
        assertEquals(updatedUser.getLastName(), user.getLastName());
        assertEquals(updatedUser.getEmail(), user.getEmail());
        assertEquals(updatedUser.getBirthDate(), user.getBirthDate());
        assertEquals(updatedUser.getPhone(), user.getPhone());
        assertEquals(updatedUser.getAddress(), user.getAddress());
    }

    @Test
    void testUpdate_WhenEmailAlreadyExists_ShouldThrowException() {
        // Prepare
        long userId = 1L;
        var existingUser = userService.getById(2L);
        var user = FakeDataGenerator.userBuilder()
                .email(existingUser.getEmail())
                .build();

        // Execute & Assert
        assertThrows(EntityAlreadyExistsException.class, () -> userService.update(userId, user));
    }

    @Test
    void testUpdate_WhenUserNotFound_ShouldThrowException() {
        // Prepare
        long userId = 1_000_000L;
        var user = FakeDataGenerator.userBuilder().build();

        // Execute & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.update(userId, user));
    }

    @Test
    void testDelete() {
        // Prepare
        long userId = 1L;

        // Execute
        userService.deleteById(userId);

        // Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    void testDelete_WhenUserNotFound_ShouldThrowException() {
        // Prepare
        long userId = 1_000_000L;

        // Execute & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deleteById(userId));
    }

    @Test
    void testUpdateFirstName() {
        // Prepare
        long userId = 1L;
        var newFirstName = "John";

        // Execute
        var updatedUser = userService.updateFirstName(userId, newFirstName);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(updatedUser.getFirstName(), newFirstName);
    }

    @Test
    void testUpdateFirstName_WhenUserNotFound_ShouldThrowException() {
        // Prepare
        long userId = 1_000_000L;
        var newFirstName = "John";

        // Execute & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateFirstName(userId, newFirstName));
    }

    @Test
    void testUpdateLastName() {
        // Prepare
        long userId = 1L;
        var newLastName = "Doe";

        // Execute
        var updatedUser = userService.updateLastName(userId, newLastName);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(updatedUser.getLastName(), newLastName);
    }

    @Test
    void testUpdateLastName_WhenUserNotFound_ShouldThrowException() {
        // Prepare
        long userId = 1_000_000L;
        var newLastName = "Doe";

        // Execute & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateLastName(userId, newLastName));
    }

    @Test
    void testUpdateAddress() {
        // Prepare
        long userId = 1L;
        var newAddress = "Liberty Street";

        // Execute
        var updatedUser = userService.updateAddress(userId, newAddress);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(updatedUser.getAddress(), newAddress);
    }

    @Test
    void testUpdateAddress_WhenUserNotFound_ShouldThrowException() {
        // Prepare
        long userId = 1_000_000L;
        var newAddress = "Liberty Street";

        // Execute & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateAddress(userId, newAddress));
    }

    @Test
    void testUpdatePhone() {
        // Prepare
        long userId = 1L;
        var newPhone = "111-222-333";

        // Execute
        var updatedUser = userService.updatePhone(userId, newPhone);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(updatedUser.getPhone(), newPhone);
    }

    @Test
    void testUpdatePhone_WhenUserNotFound_ShouldThrowException() {
        // Prepare
        long userId = 1_000_000L;
        var newPhone = "111-222-333";

        // Execute & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updatePhone(userId, newPhone));
    }

    @Test
    void testUpdateBirthDate() {
        // Prepare
        long userId = 1L;
        var newBirthDate = LocalDate.of(1991, 1, 1);

        // Execute
        var updatedUser = userService.updateBirthdate(userId, newBirthDate);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(updatedUser.getBirthDate(), newBirthDate);
    }

    @Test
    void testUpdateBirthDate_WhenUserNotFound_ShouldThrowException() {
        // Prepare
        long userId = 1_000_000L;
        var newBirthDate = LocalDate.of(1991, 1, 1);

        // Execute & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateBirthdate(userId, newBirthDate));
    }

    @Test
    void testUpdateEmail() {
        // Prepare
        long userId = 1L;
        var newEmail = "test@test.com";

        // Execute
        var updatedUser = userService.updateEmail(userId, newEmail);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(updatedUser.getEmail(), newEmail);
    }

    @Test
    void testUpdateEmail_WhenUserNotFound_ShouldThrowException() {
        // Prepare
        long userId = 1_000_000L;
        var newEmail = "test@test.com";

        // Execute & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateEmail(userId, newEmail));
    }

    @Test
    void testUpdateEmail_WhenEmailAlreadyExists_ShouldThrowException() {
        // Prepare
        long userId = 1L;
        var existingEmail = userService.getById(2L).getEmail();

        // Execute & Assert
        assertThrows(EntityAlreadyExistsException.class, () -> userService.updateEmail(userId, existingEmail));
    }
}
