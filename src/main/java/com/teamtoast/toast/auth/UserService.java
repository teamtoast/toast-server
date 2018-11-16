package com.teamtoast.toast.auth;

import com.teamtoast.toast.auth.User;
import com.teamtoast.toast.auth.UserRepository;
import com.teamtoast.toast.auth.exceptions.AuthenticationException;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;

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

    @Transactional
    public long createUser(String email, String password, String nickname, String contact, User.Gender gender, int age) {
        User user = new User(nickname, contact, gender, age);
        repository.save(user);
        try {
            byte[] hash = new SHA3.Digest256().digest(password.getBytes("UTF-8"));
            repository.saveToastAccount(user.getId(), email, hash);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //repository.saveToastAccount(user.getEmail(), email, password);
        return user.getId();
    }

    public User getUser(long id) {
        return repository.findById(id).get();
    }

    public long getUserByIdAndPassword(String email, String password) throws AuthenticationException {
        try {
            byte[] hash = new SHA3.Digest256().digest(password.getBytes("UTF-8"));
            Long result = repository.findIdByEmailAndPassword(email, hash);
            if(result == null)
                throw new AuthenticationException();

            return result;
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
