package com.nuppin.company.model;

import java.util.List;

public class Validation {

    private String validation_id;
    private String company_id;
    private String status;
    private List<ValidationMessage> messages;

    public List<ValidationMessage> getMessages() {
        return messages;
    }

    public String getValidation_id() {
        return validation_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getStatus() {
        return status;
    }
}
