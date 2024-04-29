package com.clearsolutions.usermanager.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.validation")
@Data
public class ValidationProperties {
    private Integer minimalAge;
}
