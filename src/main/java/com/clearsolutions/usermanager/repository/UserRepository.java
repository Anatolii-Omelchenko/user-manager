package com.clearsolutions.usermanager.repository;

import com.clearsolutions.usermanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE (COALESCE(:from, u.birthDate) <= u.birthDate) AND (COALESCE(:to, u.birthDate) >= u.birthDate)")
    Page<User> findUserByBirthDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to, Pageable pageable);

    boolean existsByEmail(String email);

}
