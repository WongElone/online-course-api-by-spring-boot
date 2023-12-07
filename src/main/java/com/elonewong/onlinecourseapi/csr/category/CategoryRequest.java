package com.elonewong.onlinecourseapi.csr.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record CategoryRequest (
        @NotBlank @Size(min = 3, max = 55)
        String name,
        @NotNull
        List<String> courseIds
) {}
