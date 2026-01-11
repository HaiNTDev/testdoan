package com.example.Automated.Application.Mangament.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportAccountDTO {
    @NotBlank private String userName;
    @NotBlank private String password;
    @NotBlank
    private String gmail;
    @NotBlank
    private String role;
    private String positionName;
    private String departmentName;
}
