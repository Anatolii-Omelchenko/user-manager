package com.clearsolutions.usermanager.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogLevel {
    ERROR("\033[1;91m"), // Red
    WARNING("\033[1;93m"), // Yellow
    INFO("\033[1;96m"); // Cyan

    private final String colorCode;
}
