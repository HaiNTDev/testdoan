package com.example.Automated.Application.Mangament.dto.request;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleCriteria {
   private  String field;
   private String required_criteria;
   private String rule_name;
}
