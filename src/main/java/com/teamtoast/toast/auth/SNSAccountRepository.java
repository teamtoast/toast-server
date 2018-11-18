package com.teamtoast.toast.auth;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SNSAccountRepository extends CrudRepository<SNSAccount, Long> {

    Optional<SNSAccount> findBySnsIdAndSnsType(String snsId, User.AccountType snsType);

}
