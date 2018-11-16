package com.teamtoast.toast.auth;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query(value = "INSERT INTO toast_accounts (`id`, `email`, `password`) VALUES (?1, ?2, ?3)", nativeQuery = true)
    void saveToastAccount(long id, String email, byte[] password);

    @Query(value = "SELECT id FROM toast_accounts WHERE `email`=?1 AND `password`=?2", nativeQuery = true)
    Long findIdByEmailAndPassword(String email, byte[] password);

}
