package com.example.Automated.Application.Mangament.dto.response;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Extract_Data_Response {
    private long extract_data_id;
    private String extract_data_name;
    private String extract_Data_value;
}
