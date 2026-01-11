package com.example.Automated.Application.Mangament.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
@AllArgsConstructor
@NoArgsConstructor

public class AccountResponse {
    private long accountId;
    private String userName;
    private String image;
    private String gmail;
    private boolean Active;

    public AccountResponse(long accountId, String userName, String image, String gmail) {
        this.accountId = accountId;
        this.userName = userName;
        this.image = image;
        this.gmail = gmail;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }
}
