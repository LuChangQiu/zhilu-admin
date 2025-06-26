package com.zl.mjga.dto.knowledge;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record DocUpdateDto(@NotNull Long id, @NotEmpty Boolean enable) {}
