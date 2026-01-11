package com.example.Automated.Application.Mangament.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentResponse {
    private Long id;
    private String departmentName;
    private String departmentDescription;
    private String departmentImage;

    public DepartmentResponse(Long id, String departmentName, String departmentImage, String departmentDescription) {
        this.id = id;
        this.departmentName = departmentName;
        this.departmentImage = departmentImage;
        this.departmentDescription = departmentDescription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentDescription() {
        return departmentDescription;
    }

    public void setDepartmentDescription(String departmentDescription) {
        this.departmentDescription = departmentDescription;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentImage() {
        return departmentImage;
    }

    public void setDepartmentImage(String departmentImage) {
        this.departmentImage = departmentImage;
    }
}
