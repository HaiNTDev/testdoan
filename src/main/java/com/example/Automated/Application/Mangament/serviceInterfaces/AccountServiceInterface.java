package com.example.Automated.Application.Mangament.serviceInterfaces;

import com.example.Automated.Application.Mangament.dto.request.*;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public interface AccountServiceInterface {
    ResponseEntity<ResponseObj> verifyOtpAndLogin(OtpVerifyDTO verifyDTO, HttpServletRequest request);

    public ResponseEntity<ResponseObj> login(LoginDTO loginDTO);

    public ResponseEntity<ResponseObj> createAccount(AccountDTO accountDTO);

    public ResponseEntity<ResponseObj> getAllAccount();

    public ResponseEntity<ResponseObj> findAccountById(String accountId);

    public ResponseEntity<ResponseObj> updateAccount();

    public ResponseEntity<ResponseObj> deleteAccountById(String accountId);

    ResponseEntity<ResponseObj> multipleAccounts(MultipleAccountDTO request);

    public ResponseEntity<ResponseObj> getAccountProfile();

}
