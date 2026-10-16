package com.example.talentbridge.access.presentation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "API path is required")
    private String apiPath;

    @NotBlank(message = "HTTP method is required")
    private String method;

    @NotBlank(message = "Module is required")
    private String module;
}
