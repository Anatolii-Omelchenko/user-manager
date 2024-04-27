package com.clearsolutions.usermanager.controller;

import com.clearsolutions.usermanager.exceptions.custom.EntityAlreadyExistsException;
import com.clearsolutions.usermanager.exceptions.custom.EntityNotFoundException;
import com.clearsolutions.usermanager.model.User;
import com.clearsolutions.usermanager.service.UserService;
import com.clearsolutions.usermanager.testutils.FakeDataGenerator;
import com.clearsolutions.usermanager.testutils.enums.UserFieldName;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.clearsolutions.usermanager.constants.ValidationMessages.*;
import static com.clearsolutions.usermanager.testutils.enums.UserFieldName.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Testing UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private static List<User> users;
    private static final String REQUEST_URI = "/api/users";
    private static final LocalDate DEFAULT_FROM_DATE = LocalDate.of(1900, 1, 1);
    private static final LocalDate DEFAULT_TO_DATE = LocalDate.of(2200, 1, 1);

    @BeforeAll
    static void setUp() {
        users = FakeDataGenerator.getUsers();
    }

    @SneakyThrows
    @Test
    @DisplayName("Method getUsersByBirthDateRange should return 200 with list of users when valid date range provided.")
    void getUsersByBirthDateRange_WithValidDateRange_ShouldReturnUsers() {
        // Prepare
        var fromDate = "1999-10-01";
        var toDate = "2020-10-01";

        when(userService.findUsersByBirthDateRange(LocalDate.parse(fromDate), LocalDate.parse(toDate)))
                .thenReturn(users);

        // Act & Assert
        mvc.perform(get(REQUEST_URI)
                        .param("from", fromDate)
                        .param("to", toDate))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.size()").value(users.size()))
                .andExpect(jsonPath("$[0].firstName").value(users.get(0).getFirstName()))
                .andDo(print());
    }

    @SneakyThrows
    @Test
    @DisplayName("""
            Method getUsersByBirthDateRange should return 200 with empty list
            when no users are found for the specified date range.""")
    void getUsersByBirthDateRange_WithValidDateRange_ShouldReturnEmptyList() {
        // Prepare
        var fromDate = "1999-10-01";
        var toDate = "2020-10-01";

        when(userService.findUsersByBirthDateRange(LocalDate.parse(fromDate), LocalDate.parse(toDate)))
                .thenReturn(List.of());

        // Act & Assert
        mvc.perform(get(REQUEST_URI)
                        .param("from", fromDate)
                        .param("to", toDate))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.size()").value(0))
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method getUsersByBirthDateRange should return a 200 and a list of users with the default values")
    @ParameterizedTest(name = "when 'from' is {0} and 'to' is {1}")
    @MethodSource("dateParamsProvider")
    void getUsersByBirthDateRange_ShouldReturnUsers(LocalDate from, LocalDate to) {
        // Prepare
        var fromDate = from != null ? from : DEFAULT_FROM_DATE;
        var toDate = to != null ? to : DEFAULT_TO_DATE;

        when(userService.findUsersByBirthDateRange(fromDate, toDate)).thenReturn(users);

        // Act & Assert
        mvc.perform(get(REQUEST_URI)
                        .param("from", from != null ? from.toString() : "")
                        .param("to", to != null ? to.toString() : ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.size()").value(users.size()))
                .andExpect(jsonPath("$[0].firstName").value(users.get(0).getFirstName()))
                .andDo(print());
    }

    private static Stream<Arguments> dateParamsProvider() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, LocalDate.parse("2020-10-01")),
                Arguments.of(LocalDate.parse("2020-10-01"), null)
        );
    }

    @SneakyThrows
    @DisplayName("Method getUsersByBirthDateRange should return 400")
    @ParameterizedTest(name = "When date 'from' is {0} and date 'to' is {1}")
    @MethodSource("getInvalidDateRange")
    void getUsersByBirthDateRange_WithInvalidDateRange_ShouldReturnBadRequest(String from, String to) {
        // Act & Assert
        mvc.perform(get(REQUEST_URI)
                        .param("from", from)
                        .param("to", to))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).findUsersByBirthDateRange(any(LocalDate.class), any(LocalDate.class));
    }

    private static Stream<Arguments> getInvalidDateRange() {
        var invalidDate = "xxxx-xx-xx";
        var validDate = "2020-10-01";

        return Stream.of(
                Arguments.of(validDate, invalidDate),
                Arguments.of(invalidDate, validDate),
                Arguments.of(invalidDate, invalidDate)
        );
    }

    @SneakyThrows
    @DisplayName("Method createUser should return 201 when input data is valid")
    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() {
        // Prepare
        var user = FakeDataGenerator.userBuilder().build();

        when(userService.create(any(User.class))).thenReturn(user);

        // Act & Assert
        mvc.perform(post(REQUEST_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method createUser should return 400 when input data is missing")
    @Test
    void createUser_WithoutProvidedData_ShouldReturnBadRequest() {
        // Act & Assert
        mvc.perform(post(REQUEST_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).create(any(User.class));
    }

    @SneakyThrows
    @DisplayName("Method createUser should return 400 when user is not adult")
    @Test
    void createUser_WhenUserIsNotAdult_ShouldReturnBadRequest() {
        // Prepare
        var errorMessage = "User must be at least 18 years old";
        var user = FakeDataGenerator.userBuilder()
                .birthDate(LocalDate.of(2021, 1, 1))
                .build();

        when(userService.create(any(User.class))).thenReturn(user);

        // Act & Assert
        mvc.perform(post(REQUEST_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).create(any(User.class));
    }

    @SneakyThrows
    @DisplayName("Method createUser should return 400 when user already exists")
    @Test
    void createUser_WhenUserAlreadyExists_ShouldReturnBadRequest() {
        // Prepare
        var testEmail = "test@test.com";
        var errorMessage = "User with 'Email: " + testEmail + "' already exists!";
        var user = FakeDataGenerator.userBuilder()
                .email(testEmail)
                .build();

        when(userService.create(any(User.class)))
                .thenThrow(new EntityAlreadyExistsException(User.class.getSimpleName(), "Email: " + testEmail));

        // Act & Assert
        mvc.perform(post(REQUEST_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidUserFieldsProvider")
    void createUser_WithInvalidFields_ShouldReturnBadRequest(UserFieldName fieldName, String errorMessage) {
        // Prepare
        var user = FakeDataGenerator.userBuilder()
                .withInvalid(fieldName)
                .build();

        // Act & Assert
        mvc.perform(post(REQUEST_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).create(any(User.class));
    }

    @SneakyThrows
    @DisplayName("Method updateUser should return 200 when input data is valid and user exists")
    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var user = FakeDataGenerator.userBuilder().build();

        when(userService.update(anyLong(), any(User.class))).thenReturn(user);

        // Act & Assert
        mvc.perform(put(REQUEST_URI + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method updateUser should return 400 when input data is missing")
    @Test
    void updateUser_WithoutProvidedData_ShouldReturnBadRequest() {
        // Prepare
        long userId = 1L;

        // Act & Assert
        mvc.perform(put(REQUEST_URI + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).update(anyLong(), any(User.class));
    }

    @SneakyThrows
    @DisplayName("Method updateUser should return 400 when id is not valid")
    @ParameterizedTest
    @ValueSource(strings = {"id", "-1", "0", "_1_"})
    void updateUser_WhenIdIsNotValid_ShouldReturnBadRequest(String id) {
        // Prepare
        var user = FakeDataGenerator.userBuilder().build();

        // Act & Assert
        mvc.perform(put(REQUEST_URI + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).update(anyLong(), any(User.class));
    }

    @SneakyThrows
    @DisplayName("Method updateUser should return 404 when user was not found")
    @Test
    void updateUser_WhenUserWasNotFound_ShouldReturnNotFound() {
        // Prepare
        long userId = 1L;
        var user = FakeDataGenerator.userBuilder().build();
        var errorMessage = "User with `Id: " + userId + "` was not found!";

        when(userService.update(anyLong(), any(User.class)))
                .thenThrow(new EntityNotFoundException(User.class.getSimpleName(), "Id: " + userId));

        // Act & Assert
        mvc.perform(put(REQUEST_URI + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(user)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method updateUser should return 400 when user is not adult")
    @Test
    void updateUser_WhenUserIsNotAdult_ShouldReturnBadRequest() {
        // Prepare
        long userId = 1L;
        var errorMessage = "User must be at least 18 years old";
        var user = FakeDataGenerator.userBuilder()
                .birthDate(LocalDate.of(2021, 1, 1))
                .build();

        when(userService.update(anyLong(), any(User.class))).thenReturn(user);

        // Act & Assert
        mvc.perform(put(REQUEST_URI + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).update(anyLong(), any(User.class));
    }

    @SneakyThrows
    @DisplayName("Method updateUser should return 400 when user already exists")
    @Test
    void updateUser_WhenUserAlreadyExists_ShouldReturnBadRequest() {
        // Prepare
        long userId = 1L;
        var testEmail = "test@test.com";
        var errorMessage = "User with 'Email: " + testEmail + "' already exists!";
        var user = FakeDataGenerator.userBuilder()
                .email(testEmail)
                .build();

        when(userService.update(anyLong(), any(User.class)))
                .thenThrow(new EntityAlreadyExistsException(User.class.getSimpleName(), "Email: " + testEmail));

        // Act & Assert
        mvc.perform(put(REQUEST_URI + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("invalidUserFieldsProvider")
    void updateUser_WithInvalidFields_ShouldReturnBadRequest(UserFieldName fieldName, String errorMessage) {
        // Prepare
        long userId = 1L;
        var user = FakeDataGenerator.userBuilder()
                .withInvalid(fieldName)
                .build();

        // Act & Assert
        mvc.perform(put(REQUEST_URI + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).update(anyLong(), any(User.class));
    }

    private static Stream<Arguments> invalidUserFieldsProvider() {
        return Stream.of(
                Arguments.of(EMAIL, INVALID_EMAIL_FORMAT),
                Arguments.of(FIRST_NAME, FIRST_NAME_REQUIRED),
                Arguments.of(LAST_NAME, LAST_NAME_REQUIRED),
                Arguments.of(BIRTH_DATE, BIRTH_DATE_REQUIRED)
        );
    }

    @SneakyThrows
    @DisplayName("Method delete should return 204 when user exists")
    @Test
    void deleteUser_WithValidData_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;

        // Act & Assert
        mvc.perform(delete(REQUEST_URI + "/{id}", userId))
                .andExpect(status().isNoContent())
                .andDo(print());

        // Verify
        verify(userService).deleteById(userId);
    }

    @SneakyThrows
    @DisplayName("Method deleteUser should return 400 when id is not valid")
    @ParameterizedTest
    @ValueSource(strings = {"id", "-1", "0", "_1_"})
    void deleteUser_WithInvalidId_ShouldReturnBadRequest(String userId) {
        // Act & Assert
        mvc.perform(delete(REQUEST_URI + "/" + userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).deleteById(anyLong());
    }

    @SneakyThrows
    @DisplayName("Method deleteUser should return 404 when user was not found")
    @Test
    void deleteUser_WhenUserWasNotFound_ShouldReturnNotFound() {
        // Prepare
        long userId = 1L;
        var errorMessage = "User with `Id: " + userId + "` was not found!";

        doThrow(new EntityNotFoundException(User.class.getSimpleName(), "Id: " + userId))
                .when(userService).deleteById(userId);

        // Act & Assert
        mvc.perform(delete(REQUEST_URI + "/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method updateUserFirstName should return 200 when updating first name")
    @Test
    void updateUserFirstName_WithValidData_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newFirstName = "John";
        var updatedUser = FakeDataGenerator.userBuilder()
                .firstName(newFirstName)
                .build();

        when(userService.updateFirstName(userId, newFirstName)).thenReturn(updatedUser);

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/first-name", userId)
                        .param("firstName", newFirstName))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.firstName").value(newFirstName))
                .andDo(print());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"id", "-1", "0", "_1_"})
    void updateUserFirstName_WithInValidId_ShouldReturnBadRequest(String userId) {
        // Prepare
        var newFirstName = "John";

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/first-name", userId)
                        .param("firstName", newFirstName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updateFirstName(anyLong(), anyString());
    }

    @SneakyThrows
    @DisplayName("Method updateUserFirstName should return 400 when firstName is not valid")
    @Test
    void updateUserFirstName_WithInValidData_ShouldReturnBadRequest() {
        // Prepare
        long userId = 1L;
        var newFirstName = "";

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/first-name", userId)
                        .param("firstName", newFirstName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updateFirstName(userId, newFirstName);
    }

    @SneakyThrows
    @DisplayName("Method updateUserFirstName should return 404 when user was not found")
    @Test
    void updateUserFirstName_WhenUserWasNotFound_ShouldReturnNotFound() {
        // Prepare
        long userId = 1L;
        var newFirstName = "John";
        var errorMessage = "User with `Id: " + userId + "` was not found!";

        when(userService.updateFirstName(userId, newFirstName))
                .thenThrow(new EntityNotFoundException(User.class.getSimpleName(), "Id: " + userId));

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/first-name", userId)
                        .param("firstName", newFirstName))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method updateUserLastName should return 200 when updating last name")
    @Test
    void updateUserLastName_WithValidData_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newLastName = "Doe";
        var updatedUser = FakeDataGenerator.userBuilder()
                .lastName(newLastName)
                .build();

        when(userService.updateLastName(userId, newLastName)).thenReturn(updatedUser);

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/last-name", userId)
                        .param("lastName", newLastName))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.lastName").value(newLastName))
                .andDo(print());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"id", "-1", "0", "_1_"})
    void updateUserLastName_WithInValidId_ShouldReturnBadRequest(String userId) {
        // Prepare
        var newLastName = "Doe";

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/last-name", userId)
                        .param("lastName", newLastName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updateFirstName(anyLong(), anyString());
    }

    @SneakyThrows
    @DisplayName("Method updateUserLastName should return 400 when lastName is not valid")
    @Test
    void updateUserLastName_WithInValidData_ShouldReturnBadRequest() {
        // Prepare
        long userId = 1L;
        var newLastName = "";

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/last-name", userId)
                        .param("lastName", newLastName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updateFirstName(userId, newLastName);
    }

    @SneakyThrows
    @DisplayName("Method updateUserLastName should return 404 when user was not found")
    @Test
    void updateUserLastName_WhenUserWasNotFound_ShouldReturnNotFound() {
        // Prepare
        long userId = 1L;
        var newLastName = "John";
        var errorMessage = "User with `Id: " + userId + "` was not found!";

        when(userService.updateLastName(userId, newLastName))
                .thenThrow(new EntityNotFoundException(User.class.getSimpleName(), "Id: " + userId));

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/last-name", userId)
                        .param("lastName", newLastName))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method updateUserAddress should return 200 when updating address")
    @Test
    void updateUserAddress_WithValidData_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newAddress = "Liberty Street";
        var updatedUser = FakeDataGenerator.userBuilder()
                .address(newAddress)
                .build();

        when(userService.updateAddress(userId, newAddress)).thenReturn(updatedUser);

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/address", userId)
                        .param("address", newAddress))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.address").value(newAddress))
                .andDo(print());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"id", "-1", "0", "_1_"})
    void updateUserAddress_WithInValidId_ShouldReturnBadRequest(String userId) {
        // Prepare
        var newAddress = "Liberty Street";

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/address", userId)
                        .param("address", newAddress))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updateAddress(anyLong(), anyString());
    }

    @SneakyThrows
    @DisplayName("Method updateUserAddress should return 400 when address is not provided")
    @Test
    void updateUserAddress_WithInValidData_ShouldReturnBadRequest() {
        // Prepare
        long userId = 1L;

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/address", userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updateAddress(anyLong(), anyString());
    }

    @SneakyThrows
    @DisplayName("Method updateUserAddress should return 404 when user was not found")
    @Test
    void updateUserAddress_WhenUserWasNotFound_ShouldReturnNotFound() {
        // Prepare
        long userId = 1L;
        var newAddress = "Liberty Street";
        var errorMessage = "User with `Id: " + userId + "` was not found!";

        when(userService.updateAddress(userId, newAddress))
                .thenThrow(new EntityNotFoundException(User.class.getSimpleName(), "Id: " + userId));

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/address", userId)
                        .param("address", newAddress))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method updateUserPhone should return 200 when updating phone")
    @Test
    void updateUserPhone_WithValidData_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newPhone = "111-222-333";
        var updatedUser = FakeDataGenerator.userBuilder()
                .phone(newPhone)
                .build();

        when(userService.updatePhone(userId, newPhone)).thenReturn(updatedUser);

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/phone", userId)
                        .param("phone", newPhone))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.phone").value(newPhone))
                .andDo(print());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"id", "-1", "0", "_1_"})
    void updateUserPhone_WithInValidId_ShouldReturnBadRequest(String userId) {
        // Prepare
        var newPhone = "111-222-333";

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/phone", userId)
                        .param("phone", newPhone))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updatePhone(anyLong(), anyString());
    }

    @SneakyThrows
    @DisplayName("Method updateUserPhone should return 400 when address is not provided")
    @Test
    void updateUserPhone_WithInValidData_ShouldReturnBadRequest() {
        // Prepare
        long userId = 1L;

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/phone", userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updatePhone(anyLong(), anyString());
    }

    @SneakyThrows
    @DisplayName("Method updateUserPhone should return 404 when user was not found")
    @Test
    void updateUserPhone_WhenUserWasNotFound_ShouldReturnNotFound() {
        // Prepare
        long userId = 1L;
        var newPhone = "111-222-333";
        var errorMessage = "User with `Id: " + userId + "` was not found!";

        when(userService.updatePhone(userId, newPhone))
                .thenThrow(new EntityNotFoundException(User.class.getSimpleName(), "Id: " + userId));

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/phone", userId)
                        .param("phone", newPhone))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method updateUserBirthDate should return 200 when updating birth-date")
    @Test
    void updateUserBirthDate_WithValidData_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newBirtDate = LocalDate.of(1999,1,1);
        var updatedUser = FakeDataGenerator.userBuilder()
                .birthDate(newBirtDate)
                .build();

        when(userService.updateBirthdate(userId, newBirtDate)).thenReturn(updatedUser);

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/birth-date", userId)
                        .param("birthDate", String.valueOf(newBirtDate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.birthDate").value(String.valueOf(newBirtDate)))
                .andDo(print());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"id", "-1", "0", "_1_"})
    void updateUserBirthDate_WithInValidId_ShouldReturnBadRequest(String userId) {
        // Prepare
        var newBirtDate = LocalDate.of(1999,1,1);

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/birth-date", userId)
                        .param("birthDate", String.valueOf(newBirtDate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updateBirthdate(anyLong(), any(LocalDate.class));
    }

    @SneakyThrows
    @DisplayName("Method updateUserBirthDate should return 400 when birth-date is not valid")
    @Test
    void updateUserBirthDate_WithInValidData_ShouldReturnBadRequest() {
        // Prepare
        long userId = 1L;
        var newBirthDate = "";

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/birth-date", userId)
                        .param("birthDate", newBirthDate))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updateBirthdate(anyLong(), any(LocalDate.class));
    }

    @SneakyThrows
    @DisplayName("Method updateUserBirthDate should return 404 when user was not found")
    @Test
    void updateUserBirthDate_WhenUserWasNotFound_ShouldReturnNotFound() {
        // Prepare
        long userId = 1L;
        var newBirtDate = LocalDate.of(1999,1,1);
        var errorMessage = "User with `Id: " + userId + "` was not found!";

        when(userService.updateBirthdate(userId, newBirtDate))
                .thenThrow(new EntityNotFoundException(User.class.getSimpleName(), "Id: " + userId));

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/birth-date", userId)
                        .param("birthDate", String.valueOf(newBirtDate)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method updateUserBirthDate should return 400 when user is not adult")
    @Test
    void updateUserBirthDate_WhenUserIsNotAdult_ShouldReturnBadRequest() {
        // Prepare
        long userId = 1L;
        var newBirtDate = LocalDate.of(2020,1,1);
        var errorMessage = "User must be at least 18 years old.";

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/birth-date", userId)
                        .param("birthDate", String.valueOf(newBirtDate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method updateUserEmail should return 200 when updating email")
    @Test
    void updateUserEmail_WithValidData_ShouldReturnUpdatedUser() {
        // Prepare
        long userId = 1L;
        var newEmail = "test@test.com";
        var updatedUser = FakeDataGenerator.userBuilder()
                .email(newEmail)
                .build();

        when(userService.updateEmail(userId, newEmail)).thenReturn(updatedUser);

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/email", userId)
                        .param("email", newEmail))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.email").value(newEmail))
                .andDo(print());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"id", "-1", "0", "_1_"})
    void updateUserEmail_WithInValidId_ShouldReturnBadRequest(String userId) {
        // Prepare
        var newEmail = "test@test.com";

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/email", userId)
                        .param("email", newEmail))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updateEmail(anyLong(), anyString());
    }

    @SneakyThrows
    @DisplayName("Method updateUserEmail should return 400 when email is not valid")
    @Test
    void updateUserEmail_WithInValidData_ShouldReturnBadRequest() {
        // Prepare
        long userId = 1L;
        var newEmail = "";

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/email", userId)
                        .param("email", newEmail))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());

        // Verify
        verify(userService, never()).updateEmail(anyLong(), anyString());
    }

    @SneakyThrows
    @DisplayName("Method updateUserEmail should return 404 when user was not found")
    @Test
    void updateUserEmail_WhenUserWasNotFound_ShouldReturnNotFound() {
        // Prepare
        long userId = 1L;
        var newEmail = "test@test.com";
        var errorMessage = "User with `Id: " + userId + "` was not found!";

        when(userService.updateEmail(userId, newEmail))
                .thenThrow(new EntityNotFoundException(User.class.getSimpleName(), "Id: " + userId));

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/email", userId)
                        .param("email", newEmail))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @SneakyThrows
    @DisplayName("Method updateUserEmail should return 400 when user already exists")
    @Test
    void updateUserEmail_WhenUserAlreadyExists_ShouldReturnBadRequest() {
        // Prepare
        long userId = 1L;
        var newEmail = "test@test.com";
        var errorMessage = "User with 'Email: " + newEmail + "' already exists!";

        when(userService.updateEmail(userId, newEmail))
                .thenThrow(new EntityAlreadyExistsException(User.class.getSimpleName(), "Email: " + newEmail));

        // Act & Assert
        mvc.perform(patch(REQUEST_URI + "/{id}/email", userId)
                        .param("email", newEmail))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(containsString(errorMessage)))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }
}
