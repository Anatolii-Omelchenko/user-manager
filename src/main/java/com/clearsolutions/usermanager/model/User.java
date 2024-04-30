package com.clearsolutions.usermanager.model;

import com.clearsolutions.usermanager.dto.annotation.OnlyAdult;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

import static com.clearsolutions.usermanager.constants.ValidationMessages.*;

@Builder
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = FIRST_NAME_REQUIRED)
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = LAST_NAME_REQUIRED)
    @Column(nullable = false)
    private String lastName;

    @Email(message = INVALID_EMAIL_FORMAT)
    @NotBlank(message = EMAIL_REQUIRED)
    @Column(unique = true, nullable = false)
    private String email;

    @OnlyAdult
    @NotNull(message = BIRTH_DATE_REQUIRED)
    @Past(message = BIRTH_DATE_PAST)
    @Column(nullable = false)
    private LocalDate birthDate;

    private String address;

    private String phone;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ?
                proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
