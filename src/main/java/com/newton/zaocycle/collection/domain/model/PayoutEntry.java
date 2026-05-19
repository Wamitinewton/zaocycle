package com.newton.zaocycle.collection.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayoutEntry(BigDecimal amount, LocalDate date) {}
