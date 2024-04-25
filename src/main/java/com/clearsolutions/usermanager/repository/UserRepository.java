package com.clearsolutions.usermanager.repository;

import com.clearsolutions.usermanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findUserByBirthDateBetween(LocalDate from, LocalDate to);

    boolean existsByEmail(String email);

}
