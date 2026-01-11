package com.example.Automated.Application.Mangament.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Data
public class Account extends BaseEntity implements UserDetails {
    @Column(name = "user_name")

    private String userName;

    private String password;

    private String accountImage;

    private String gmail;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountImage() {
        return accountImage;
    }

    public void setAccountImage(String accountImage) {
        this.accountImage = accountImage;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<AccountPosition> getAccountPositionList() {
        return accountPositionList;
    }

    public void setAccountPositionList(List<AccountPosition> accountPositionList) {
        this.accountPositionList = accountPositionList;
    }

    public List<TraineeApplication> getTraineeAplicationList() {
        return traineeAplicationList;
    }

    public void setTraineeAplicationList(List<TraineeApplication> traineeAplicationList) {
        this.traineeAplicationList = traineeAplicationList;
    }

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonIgnore
    private Role role;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AccountPosition> accountPositionList;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TraineeApplication> traineeAplicationList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return super.isActive();
    }

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private AccountProfile profile;

    public AccountProfile getProfile() {
        return profile;
    }

    public void setProfile(AccountProfile profile) {
        this.profile = profile;
    }
}
