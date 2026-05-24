package com.workspace.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Record — immutable, tự generate constructor/getter/equals/hashCode
// Dùng cho data đi vào use case
public record RegisterRequest(

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Password không được để trống")
        @Size(min = 8, message = "Password phải có ít nhất 8 ký tự")
        String password

) {}