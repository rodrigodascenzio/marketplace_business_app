package com.nuppin.company.model;

public class ValidationMessage {

    private String validation_message_id;
    private String validation_id;
    private String message;
    private String status;

    public String getValidation_id() {
        return validation_id;
    }

    public String getValidation_message_id() {
        return validation_message_id;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
