package com.example.Automated.Application.Mangament.dto.request;


import com.example.Automated.Application.Mangament.enums.RoleEnum;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private RoleEnum roleName;

    public RoleEnum getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleEnum roleName) {
        this.roleName = roleName;
    }
}
