package com.example.library.more;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountMore {
    private String username;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String type;
    public AccountMore(String username, String email, String phone, String address, String password, String type){
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.password = password;
        this.type = type;
    }
}
