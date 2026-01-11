package com.example.Automated.Application.Mangament.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtractDataResponse {
    private Long extract_data_id;
    private String extract_data_name;
    private String extract_Data_value;
}
