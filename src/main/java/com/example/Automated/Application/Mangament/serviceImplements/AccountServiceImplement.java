package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.dto.request.*;
import com.example.Automated.Application.Mangament.dto.response.AccountProfileResponse;
import com.example.Automated.Application.Mangament.dto.response.MultipleAccountResponse;
import com.example.Automated.Application.Mangament.dto.response.ResponseObj;
import com.example.Automated.Application.Mangament.enums.RoleEnum;
import com.example.Automated.Application.Mangament.enums.StatusEnum;
import com.example.Automated.Application.Mangament.exception.AppException;
import com.example.Automated.Application.Mangament.exception.ErrorCode;
import com.example.Automated.Application.Mangament.model.*;
import com.example.Automated.Application.Mangament.repositories.*;
import com.example.Automated.Application.Mangament.dto.response.AccountResponse;
import com.example.Automated.Application.Mangament.serviceInterfaces.AccountServiceInterface;
import com.example.Automated.Application.Mangament.serviceInterfaces.EmailServiceInterface;
import com.example.Automated.Application.Mangament.serviceInterfaces.TraineeApplicationServiceInterface;
import com.example.Automated.Application.Mangament.utils.AuthenUntil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountServiceImplement implements AccountServiceInterface {
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_-]+$";

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TraineeApplicationServiceInterface traineeApplicationServiceInterface;

    @Autowired
    private AuthenUntil authenUntil;

    @Autowired
    private JwtServiceImplement jwtServiceImplement;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private AccountPositionRepository accountPositionRepository;

    @Autowired
    private TraineeApplicationRepository traineeApplicationRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private EmailServiceInterface emailService;
//    @Override
//    public ResponseEntity<ResponseObj> login(LoginDTO loginDTO) {
//        try{
//            if(accountRepository.findByUserName(loginDTO.getUserName())== null){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObj(HttpStatus.UNAUTHORIZED.toString(), "User name does not exist", null));
//            }
//
//            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUserName(), loginDTO.getPassword()));
//
//            if(authentication.isAuthenticated()){
//                if(!accountRepository.findByUserName(loginDTO.getUserName()).get().isActive()){
//                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "This account get banned", null));
//                }
//                String token = jwtServiceImplement.generateToken(loginDTO.getUserName());
//                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.toString(), "Login successfully", token));
//            }
//
//        }catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new ResponseObj(HttpStatus.UNAUTHORIZED.toString(), "Login failed: Incorrect password", null));
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new ResponseObj(HttpStatus.UNAUTHORIZED.toString(), "Login failed: " + e.getMessage(), null));
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new ResponseObj(HttpStatus.UNAUTHORIZED.toString(), "Login failed: " + e.getMessage(), null));
//
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                .body(new ResponseObj(HttpStatus.UNAUTHORIZED.toString(), "Login failed", null));
//    }

    @Override
    public ResponseEntity<ResponseObj> login(LoginDTO loginDTO) {
        try {
            long startTime = System.currentTimeMillis();
            Optional<Account> accountOpt = accountRepository.findByUserName(loginDTO.getUserName());
            if (accountOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ResponseObj(HttpStatus.UNAUTHORIZED.toString(), "User name does not exist", null)
                );
            }
            Account account = accountOpt.get();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUserName(), loginDTO.getPassword())
            );

            if(loginDTO.getUserName() != null){
                if(!loginDTO.getUserName().matches(USERNAME_REGEX)){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Username contains invalid characters", null));
                }
            }

            if (authentication.isAuthenticated()) {
                if (!account.isActive()) {
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObj(HttpStatus.OK.toString(), "This account get banned", null)
                    );
                }

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;

//                boolean hasLoggedIn = auditRepository.existsByUsernameAndApiMethodAndSuccess(account.getUserName(), "FIRST_LOGIN_SUCCESS", true);
////                if (!hasLoggedIn) {
////                    String otp = generateOtp();
////                    OtpEntity otpEntity = new OtpEntity();
////                    otpEntity.setEmail(account.getGmail());
////                    otpEntity.setOtp(otp);
////                    otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
////                    otpRepository.save(otpEntity);
////                    emailService.sendOtpEmail(account.getGmail(), otp);
////                    return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "First login detected. OTP sent to email. Please verify.", null));
////                }
////                AuditEntity audit = new AuditEntity();
////                audit.setUsername(account.getUserName());
////                audit.setApiMethod("LOGIN");
////                audit.setSuccess(true);
////                auditRepository.save(audit);

                if (loginDTO.getUserName() != null) {
                    if (!loginDTO.getUserName().matches(USERNAME_REGEX)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObj(HttpStatus.BAD_REQUEST.toString(), "Username contains invalid characters", null));
                    }
                }


                String token = jwtServiceImplement.generateToken(loginDTO.getUserName());

                System.out.println("Login successful for user: " + loginDTO.getUserName() +
                        ". Time taken: " + duration + "ms");

                if (account.getRole().getRoleName().toString().trim().equalsIgnoreCase(RoleEnum.TRAINEE.toString().trim()) && (account.getTraineeAplicationList().isEmpty() || account.getTraineeAplicationList() == null)) {
                    traineeApplicationServiceInterface.createDefaultApplication(account);
                }
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObj(
                                HttpStatus.OK.toString(),
                                "Login successful",
                                token
                        )
                );
            }
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObj(HttpStatus.UNAUTHORIZED.toString(), "Login failed: Incorrect password", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Login failed due to server error: " + e.getMessage(), null));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseObj(HttpStatus.UNAUTHORIZED.toString(), "Login failed", null));
    }


    @Transactional
    public ResponseEntity<ResponseObj> verifyOtpAndLogin(OtpVerifyDTO verifyDTO, HttpServletRequest request) {
        if (verifyDTO.getEmail() == null || verifyDTO.getEmail().isBlank()) {
            throw new AppException(ErrorCode.EMAIL_INVALID);  // Thêm validation cho email
        }
        Optional<Account> accountOpt = accountRepository.findByGmail(verifyDTO.getEmail());
        if (accountOpt.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Account account = accountOpt.get();

        // Kiểm tra thêm: Đảm bảo đây là first-login (tránh abuse)
        boolean hasLoggedIn = auditRepository.existsByUsernameAndApiMethodAndSuccess(account.getUserName(), "LOGIN", true);
        if (hasLoggedIn) {
            throw new AppException(ErrorCode.OTP_INVALID);  // Hoặc error custom: "Not first login"
        }

        if (verifyDTO.getOtp() == null || verifyDTO.getOtp().isBlank()) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }
        Optional<OtpEntity> otpOpt = otpRepository.findByEmailAndOtpAndUsedFalse(account.getGmail(), verifyDTO.getOtp());
        if (otpOpt.isEmpty()) {
            throw new AppException(ErrorCode.OTP_NOT_FOUND);
        }
        OtpEntity otpEntity = otpOpt.get();
        if (otpEntity.isExpired()) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }
        if (otpEntity.isUsed()) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);

        // Lưu audit first login success chỉ nếu verify OK
        AuditEntity audit = new AuditEntity();
        audit.setUsername(account.getUserName());
        audit.setApiMethod("FIRST_LOGIN_SUCCESS");
        audit.setSuccess(true);
        audit.setTimestamp(LocalDateTime.now());
        auditRepository.save(audit);

        // Giờ generate token và complete login
        String token = jwtServiceImplement.generateToken(account.getUserName());
        return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "OTP verified. Login complete.", token));
    }

    private String generateOtp() {
        SecureRandom secureRandom = new SecureRandom();
        int otpInt = secureRandom.nextInt(900000) + 100000; // 6 chữ số
        return String.valueOf(otpInt);
    }

    private AccountResponse convertAccountResponse(Account account){
        AccountResponse accountResponse =new AccountResponse();
        accountResponse.setAccountId(account.getId());
        accountResponse.setImage(account.getAccountImage());
        accountResponse.setActive(account.isActive());
        accountResponse.setUserName(account.getUserName());
        accountResponse.setGmail(account.getGmail());
        return accountResponse;
    }


    public Account getUserByToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            return null;
        }
        String token = authorizationHeader.substring(7);
        String userName = jwtServiceImplement.extractUsername(token);
        return accountRepository.findByUserName(userName).get();
    }

    @Override
    public ResponseEntity<ResponseObj> createAccount(AccountDTO accountDTO) {
        Account account = new Account();
        account.setUserName(accountDTO.getUserName());
        account.setAccountImage(accountDTO.getAccountImage());
        account.setGmail(account.getGmail());
        account.setPassword(bCryptPasswordEncoder.encode(accountDTO.getPassword()));
        account.setActive(true);
        account.setRole(roleRepository.findByRoleName(RoleEnum.ADMIN).get());
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObj(HttpStatus.OK.toString(), "Create account  successfully", null));

    }

    @Override
    public ResponseEntity<ResponseObj> getAllAccount() {
        try {
            List<AccountResponse> accountList = new ArrayList<>();
            List<Account> accounts =accountRepository.findAll();
            if (accounts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObj(HttpStatus.OK.toString(), "List is empty", null));
            }
            for(Account account : accounts){
                accountList.add(convertAccountResponse(account));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObj(HttpStatus.OK.toString(), "List of user", accountRepository.findAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Server error: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ResponseObj> getAccountProfile() {
        try {

            Account account = authenUntil.getCurrentUSer();
            if(account == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObj(HttpStatus.UNAUTHORIZED.toString(), "Plesae login to use this method", null));
            }
            AccountProfileResponse response = new AccountProfileResponse();
            response.setUserName(account.getUserName());
            response.setAccountImage(account.getAccountImage());
            response.setGmail(account.getGmail());

            String positionName = account.getAccountPositionList().stream()
                    .map(AccountPosition::getPosition)
                    .filter(Objects::nonNull)
                    .map(Position::getPositionName)
                    .findFirst()
                    .orElse(null);
            response.setPositionName(positionName);

            String departmentName = (account.getDepartment() != null) ? account.getDepartment().getDepartmentName() : null;
            response.setDepartmentName(departmentName);

            AccountProfile profile = account.getProfile();
            if (profile != null) {
                response.setFullName(profile.getFullName());
                response.setBirthDay(profile.getBirthDay());
                response.setAddress(profile.getAddress());
                response.setGender(profile.getGender());
            }

            return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), "Profile retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObj(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error retrieving profile: " + e.getMessage(), null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObj> multipleAccounts(MultipleAccountDTO request) {
        List<MultipleAccountResponse> failedResults = new ArrayList<>(); // Chỉ lưu failed

        int total = request.getAccounts().size();
        if (total == 0) {
            throw new AppException(ErrorCode.UNCATEGORIZED_ERROR);
        }
        Map<String, Long> usernameCount = request.getAccounts().stream()
                .collect(Collectors.groupingBy(ImportAccountDTO::getUserName, Collectors.counting()));

        Map<String, Long> emailCount = request.getAccounts().stream()
                .collect(Collectors.groupingBy(ImportAccountDTO::getGmail, Collectors.counting()));
        List<String> usernames = new ArrayList<>(usernameCount.keySet());
        List<String> emails = new ArrayList<>(emailCount.keySet());
        Set<String> existingUsernames = accountRepository.findAllByUserNameIn(usernames).stream()
                .map(Account::getUserName)
                .collect(Collectors.toSet());
        Set<String> existingEmails = accountRepository.findAllByGmailIn(emails).stream()
                .map(Account::getGmail)
                .collect(Collectors.toSet());
        Map<String, Department> deptMap = departmentRepository.findAll().stream()
                .collect(Collectors.toMap(d -> d.getDepartmentName().toUpperCase(), d -> d));

        Map<String, Position> posMap = positionRepository.findAll().stream()
                .collect(Collectors.toMap(p -> p.getPositionName().toUpperCase(), p -> p));

        Map<RoleEnum, Role> roleMap = roleRepository.findAll().stream()
                .collect(Collectors.toMap(Role::getRoleName, r -> r));

        int successCount = 0;

        for (ImportAccountDTO dto : request.getAccounts()) {
            try {
                if (usernameCount.get(dto.getUserName()) > 1) {
                    throw new AppException(ErrorCode.DUPLICATE_USERNAME_IN_BATCH);
                }
                if (emailCount.get(dto.getGmail()) > 1) {
                    throw new AppException(ErrorCode.DUPLICATE_EMAIL_IN_BATCH);
                }
                if (existingUsernames.contains(dto.getUserName())) {
                    throw new AppException(ErrorCode.USERNAME_EXISTS);
                }
                if (existingEmails.contains(dto.getGmail())) {
                    throw new AppException(ErrorCode.EMAIL_EXISTS);
                }

                String roleStr = dto.getRole();
                if (roleStr == null || roleStr.isBlank()) {
                    roleStr = "TRAINEE";
                }
                RoleEnum roleEnum = RoleEnum.valueOf(dto.getRole().toUpperCase());
                Role role = roleMap.get(roleEnum);
                if (role == null) {
                    throw new AppException(ErrorCode.ROLE_NOT_FOUND);
                }
                Department department = null;
                if (!Set.of("ADMIN", "TRAINING_DIRECTOR").contains(roleEnum.name())) {
                    if (dto.getDepartmentName() == null || dto.getDepartmentName().isBlank()) {
                        throw new AppException(ErrorCode.DEPARTMENT_REQUIRED);
                    }
                    department = deptMap.get(dto.getDepartmentName().toUpperCase());
                    if (department == null) {
                        throw new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
                    }
                }
                Position position = null;
                if (dto.getPositionName() != null) {
                    position = positionRepository.findByPositionNameIgnoreCase(dto.getPositionName())
                            .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_ERROR));


                    if (position.getDepartment().getId() != department.getId()) {
                        throw new AppException(ErrorCode.UNCATEGORIZED_ERROR);
                    }
                }
                Account account = new Account();
                account.setUserName(dto.getUserName());
                account.setPassword(passwordEncoder.encode(dto.getPassword()));
                account.setGmail(dto.getGmail());
                account.setDepartment(department);
                account.setRole(role);
                account.setActive(true);
                accountRepository.save(account);

                if (position != null) {
                    AccountPosition ap = new AccountPosition();
                    ap.setAccount(account);
                    ap.setPosition(position);
                    accountPositionRepository.save(ap);
                }
                if (roleEnum == RoleEnum.TRAINEE) {
                    TraineeApplication app = new TraineeApplication();
                    app.setAccount(account);
                    app.setStatusEnum(StatusEnum.InProgress);
                    app.setPosition(position);
                    traineeApplicationRepository.save(app);
                }

                successCount++;

            } catch (AppException e) {
                MultipleAccountResponse resError = new MultipleAccountResponse();
                resError.setUserName(dto.getUserName());
                resError.setStatus("error");
                resError.setMessage(e.getErrorCode().getMessage());
                resError.setCode(e.getErrorCode().getCode());
                failedResults.add(resError);
            } catch (Exception e) {
                MultipleAccountResponse resError = new MultipleAccountResponse();
                resError.setUserName(dto.getUserName());
                resError.setStatus("error");
                resError.setMessage("Unexpected error");
                resError.setCode(999);
                failedResults.add(resError);
            }
        }

        String message = "Import completed: " + successCount + "/" + total + " successful. Failed accounts listed below.";

        return ResponseEntity.ok(new ResponseObj(HttpStatus.OK.toString(), message, failedResults));
    }

    private AccountResponse convertUserToUserResponse(Account account){
        if(account == null)
            return null;
        AccountResponse accountResponse = new AccountResponse(account.getId(),account.getUserName(),account.getAccountImage(),account.getGmail());
        return accountResponse;
    }

    @Override
    public ResponseEntity<ResponseObj> findAccountById(String accountId) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObj> updateAccount() {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObj> deleteAccountById(String accountId) {
        return null;
    }
}
