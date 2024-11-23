package com.example.refresh;

public class ErrorMessage {
    private UserCols field;
    private String message;

    public ErrorMessage(UserCols field, String message) {
        this.field = field;
        this.message = message;
    }

    public void setField(UserCols field) {
        this.field = field;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserCols getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
