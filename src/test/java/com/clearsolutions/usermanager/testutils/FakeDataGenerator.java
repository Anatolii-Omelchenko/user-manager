package com.clearsolutions.usermanager.testutils;

import com.clearsolutions.usermanager.model.User;
import com.clearsolutions.usermanager.testutils.enums.UserFieldName;
import com.github.javafaker.Faker;
import lombok.Getter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FakeDataGenerator {

    private static final Faker FAKER = new Faker();

    @Getter
    private static final List<User> users = new ArrayList<>();

    private FakeDataGenerator() {
    }

    static {
        users.addAll(generateUsers());
    }

    static List<User> generateUsers() {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            userList.add(userBuilder().build());
        }
        return userList;
    }

    public static UserBuilder userBuilder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private String firstName = FAKER.name().firstName();
        private String lastName = FAKER.name().lastName();
        private String email = FAKER.internet().emailAddress();
        private LocalDate birthDate = FAKER.date().birthday(18, 140)
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        private String address = FAKER.address().fullAddress();
        private String phone = FAKER.phoneNumber().phoneNumber();

        public UserBuilder withInvalid(UserFieldName... fields) {
            Arrays.stream(fields)
                    .forEach(field -> {
                        switch (field) {
                            case FIRST_NAME -> this.firstName = null;
                            case LAST_NAME -> this.lastName = null;
                            case EMAIL -> this.email = "invalid_email";
                            case BIRTH_DATE -> this.birthDate = null;
                        }
                    });
            return this;
        }

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public UserBuilder address(String address) {
            this.address = address;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public User build() {
            return User.builder()
                    .firstName(this.firstName)
                    .lastName(this.lastName)
                    .email(this.email)
                    .birthDate(this.birthDate)
                    .phone(this.phone)
                    .address(this.address)
                    .build();
        }
    }
}
