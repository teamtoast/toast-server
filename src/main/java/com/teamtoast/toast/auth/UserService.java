package com.teamtoast.toast.auth;

import com.teamtoast.toast.auth.User;
import com.teamtoast.toast.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private SNSAccountRepository snsRepository;

    @Transactional
    public long createUser(User.AccountType type, String platformId, String nickname, String contact, User.Gender gender, int age) {
        User user = new User(nickname, contact, gender, age);
        repository.save(user);
        snsRepository.save(new SNSAccount(user.getId(), platformId, type));
        return user.getId();
    }

    public User getUser(long id) {
        return repository.findById(id).get();
    }

}
