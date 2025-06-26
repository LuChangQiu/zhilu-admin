package com.zl.mjga.dto.knowledge;

import jakarta.validation.constraints.NotEmpty;

public record LibraryUpsertDto(Long id, @NotEmpty String name, String description) {}
