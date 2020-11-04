package com.nuppin.company.model;

public class TempSms {

    private String temp_sms_id;
    private String code_sent;
    private String source = "nuppin";
    private String user_id;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setCelular(String celular) {
        this.temp_sms_id = celular;
    }

    public void setCode(String code) {
        this.code_sent = code;
    }

    public String getCelular(){
        return temp_sms_id;
    }
}
