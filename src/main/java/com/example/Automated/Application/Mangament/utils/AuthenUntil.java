package com.example.Automated.Application.Mangament.utils;

import com.example.Automated.Application.Mangament.model.Account;
import com.example.Automated.Application.Mangament.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenUntil {
    @Autowired
    private AccountRepository accountRepository;

    public Account getCurrentUSer(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        return accountRepository.findByUserName(username).orElse(null);
    }
}