package com.teamtoast.toast.auth;

import javax.persistence.*;

@Entity
@Table(name = "sns_accounts")
public class SNSAccount {

    @Id
    private long user;
    @Column(name = "sns_id")
    private String snsId;
    @Column(name = "sns_type")
    @Enumerated(EnumType.STRING)
    private User.AccountType snsType;

    public SNSAccount() {
    }

    public SNSAccount(long user, String snsId, User.AccountType snsType) {
        this.user = user;
        this.snsId = snsId;
        this.snsType = snsType;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public String getSNSId() {
        return snsId;
    }

    public void setSNSId(String snsId) {
        this.snsId = snsId;
    }

    public User.AccountType getSNSType() {
        return snsType;
    }

    public void setSNSType(User.AccountType snsType) {
        this.snsType = snsType;
    }
}
