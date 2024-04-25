package com.clearsolutions.usermanager.controller;

import com.clearsolutions.usermanager.model.User;
import com.clearsolutions.usermanager.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.clearsolutions.usermanager.utils.Validator.validateDateRange;
import static com.clearsolutions.usermanager.utils.Validator.validateUserAge;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    @Value("${validation.user.minimumAge}")
    private int minimumAge;

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getUsersByBirthDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        validateDateRange(from, to);
        var users = userService.findUsersByBirthDateRange(from, to);

        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        validateUserAge(user.getBirthDate(), minimumAge);
        var createdUser = userService.create(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable @Min(1L) Long id, @RequestBody @Valid User user) {
        validateUserAge(user.getBirthDate(), minimumAge);
        var updatedUser = userService.update(id, user);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Min(1L) Long id) {
        userService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/first-name")
    public ResponseEntity<User> updateUserFirstName(
            @PathVariable @Min(1L) Long id,
            @RequestParam @NotBlank(message = "First name cannot be blank!") String firstName) {
        var updatedUser = userService.updateFirstName(id, firstName);

        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/last-name")
    public ResponseEntity<User> updateUserLastName(
            @PathVariable @Min(1L) Long id,
            @RequestParam @NotBlank(message = "Last name cannot be blank!") String lastName) {
        var updatedUser = userService.updateLastName(id, lastName);

        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<User> updateUserEmail(
            @PathVariable @Min(1L) Long id,
            @RequestParam @NotBlank @Email String email) {
        var updatedUser = userService.updateEmail(id, email);

        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/birth-date")
    public ResponseEntity<User> updateUserBirthDate(
            @PathVariable @Min(1L) Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Past LocalDate birthDate) {
        validateUserAge(birthDate, minimumAge);
        var updatedUser = userService.updateBirthdate(id, birthDate);

        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/address")
    public ResponseEntity<User> updateUserAddress(@PathVariable @Min(1L) Long id, @RequestParam String address) {
        var updatedUser = userService.updateAddress(id, address);

        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/phone")
    public ResponseEntity<User> updateUserPhone(@PathVariable @Min(1L) Long id, @RequestParam String phone) {
        var updatedUser = userService.updatePhone(id, phone);

        return ResponseEntity.ok(updatedUser);
    }

}
