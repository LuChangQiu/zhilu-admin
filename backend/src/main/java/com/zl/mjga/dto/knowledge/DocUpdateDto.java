package com.zl.mjga.dto.knowledge;

import jakarta.validation.constraints.NotNull;

public record DocUpdateDto(@NotNull Long id, @NotNull Long libId, @NotNull Boolean enable) {}
