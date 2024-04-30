package com.clearsolutions.usermanager.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ValidationProperties {

    @Getter
    private static Integer minimalAge;

    @Value("${app.validation.minimalAge}")
    public void setMinimalAge(Integer minimalAge) {
        ValidationProperties.minimalAge = minimalAge;
    }

}
