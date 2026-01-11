package com.example.Automated.Application.Mangament.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private String departmentName;
    private String departmentDescription;
    private String departmentImage;

    public String getDepartmentName() {
        return departmentName;
    }

    public String getDepartmentDescription() {
        return departmentDescription;
    }

    public String getDepartmentImage() {
        return departmentImage;
    }

    public void setDepartmentDescription(String departmentDescription) {
        this.departmentDescription = departmentDescription;
    }

    public void setDepartmentImage(String departmentImage) {
        this.departmentImage = departmentImage;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
