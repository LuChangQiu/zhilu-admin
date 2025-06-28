package com.zl.mjga.dto.ai;

import com.zl.mjga.model.urp.ChatMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ChatDto(@NotNull ChatMode mode, Long libraryId, @NotEmpty String message) {}
