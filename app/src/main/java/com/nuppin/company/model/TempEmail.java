package com.nuppin.company.model;

public class TempEmail {

    private String temp_email_id;
    private String code_sent;
    private String source = "nuppin";
    private String user_id;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setEmail(String email) {
        this.temp_email_id = email;
    }

    public void setCode(String code) {
        this.code_sent = code;
    }

    public String getEmail(){
        return temp_email_id;
    }
}
