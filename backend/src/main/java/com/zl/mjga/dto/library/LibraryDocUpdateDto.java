package com.zl.mjga.dto.library;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LibraryDocUpdateDto(@NotNull Long id, @NotEmpty Boolean enable) {}
