package com.clearsolutions.usermanager.dto;

import com.clearsolutions.usermanager.dto.annotation.DateOrder;

import java.time.LocalDate;

@DateOrder
public record DateRange(LocalDate from, LocalDate to) {
}
