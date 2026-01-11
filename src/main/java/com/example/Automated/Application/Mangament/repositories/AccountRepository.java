package com.example.Automated.Application.Mangament.repositories;

import com.example.Automated.Application.Mangament.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserName (String username);

    Optional<Account> findByGmail(String gmail);

    boolean existsByUserName(String userName);
    List<Account> findAllByUserNameIn(List<String> usernames);
    List<Account> findAllByGmailIn(List<String> emails);
}
