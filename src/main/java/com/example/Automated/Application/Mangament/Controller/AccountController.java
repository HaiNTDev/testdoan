package com.example.Automated.Application.Mangament.Controller;

import com.example.Automated.Application.Mangament.dto.request.*;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.serviceImplements.TraineeApplicationServiceImplement;
import com.example.Automated.Application.Mangament.serviceInterfaces.AccountServiceInterface;
import com.example.Automated.Application.Mangament.serviceInterfaces.EmailServiceInterface;
import com.example.Automated.Application.Mangament.serviceInterfaces.RoleServiceInterface;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    AccountServiceInterface accountServiceInterface;

    @Autowired
    RoleServiceInterface roleServiceInterface;

    @Autowired
    EmailServiceInterface emailService;

    @Autowired
    TraineeApplicationServiceImplement traineeApplicationServiceImplement;

    @GetMapping("/v1/getAllUser")

    public ResponseEntity<ResponseObj> getAllUser(){
        return accountServiceInterface.getAllAccount();
    }

    @GetMapping("v1/getAllRole")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> getAllRole(){
        return roleServiceInterface.getAllRole();
    }

    @PostMapping("/v1/createRole")
    public ResponseEntity<ResponseObj> createRoe(RoleDTO roleDTO){
        return roleServiceInterface.createRole(roleDTO);
    }

    @PostMapping("/v1/authenticateAccount")
    public ResponseEntity<ResponseObj> authenticate(LoginDTO loginDTO){
        return accountServiceInterface.login(loginDTO);
    }
    @PostMapping("/v1/verify-otp")
    public ResponseEntity<ResponseObj> verify_OTP(@RequestBody OtpVerifyDTO otpVerifyDTO, HttpServletRequest httpServletRequest){
        return accountServiceInterface.verifyOtpAndLogin(otpVerifyDTO, httpServletRequest);
    }

    @PostMapping("v1/createUser")
    public ResponseEntity<ResponseObj> createAccount(AccountDTO accountDTO){
        return accountServiceInterface.createAccount(accountDTO);
    }

    @PostMapping("/multipleAccounts")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObj> multipleAccounts(@RequestBody MultipleAccountDTO request) {
        return accountServiceInterface.multipleAccounts(request);
    }

    @PostMapping(value = "/add_position_to_account", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addPositionToAccount(@RequestPart String accountId,  @RequestPart String positionId){
         long accountID = Long.parseLong(accountId);
         long position_id = Long.parseLong(positionId);
         return traineeApplicationServiceImplement.addPositiontoAccount(accountID, position_id);
    }
    @GetMapping("/profile")
    public ResponseEntity<ResponseObj> getAccountProfile() {
        return accountServiceInterface.getAccountProfile();
    }
}
