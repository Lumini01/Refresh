package com.example.refresh.Model;

import com.example.refresh.Database.UsersTable.Columns;

/**
 * Represents an error message related to a specific field.
 * Used to store and retrieve field-specific error messages during validation.
 */
public class ErrorMessage {
    private Columns field;
    private String message;

    /**
     * Constructor to initialize the ErrorMessage object with a specific field and message.
     * @param field The field that the error message is associated with.
     * @param message The error message related to the field.
     */
    public ErrorMessage(Columns field, String message) {
        this.field = field;
        this.message = message;
    }

    /**
     * Sets the field for this error message.
     * @param field The field to set.
     */
    public void setField(Columns field) {
        this.field = field;
    }

    /**
     * Sets the error message.
     * @param message The error message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the field for this error message.
     * @return The field associated with the error message.
     */
    public Columns getField() {
        return field;
    }

    /**
     * Gets the error message.
     * @return The error message.
     */
    public String getMessage() {
        return message;
    }
}
