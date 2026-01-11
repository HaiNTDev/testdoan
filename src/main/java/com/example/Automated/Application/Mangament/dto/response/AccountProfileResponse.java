package com.example.Automated.Application.Mangament.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountProfileResponse {
    private String userName;
    private String accountImage;
    private String gmail;
    private String positionName;
    private String departmentName;
    private String fullName;
    private LocalDate birthDay;
    private String address;
    private String gender;
}
