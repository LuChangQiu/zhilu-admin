package com.zl.mjga.dto.scheduler;

import jakarta.validation.constraints.NotEmpty;

public record TriggerKeyDto(@NotEmpty String name, @NotEmpty String group) {}
