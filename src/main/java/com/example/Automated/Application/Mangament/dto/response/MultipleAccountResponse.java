package com.example.Automated.Application.Mangament.dto.response;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleAccountResponse {
    private String userName;
    private String status;
    private String message;
    private Integer code;
}
