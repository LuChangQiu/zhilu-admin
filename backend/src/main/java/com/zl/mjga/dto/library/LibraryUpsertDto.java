package com.zl.mjga.dto.library;

import jakarta.validation.constraints.NotEmpty;

public record LibraryUpsertDto(Long id, @NotEmpty String name) {}
