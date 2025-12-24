package com.ecommerce.project.util;

import com.ecommerce.project.exception.BadRequestException;

public class PasswordValidator {

    private static final int MIN_LENGTH = 6;

    public static void validate(String password) {
        if (password == null || password.isEmpty()) {
            throw new BadRequestException("Password cannot be empty");
        }

        if (password.length() < MIN_LENGTH) {
            throw new BadRequestException("Password must be at least " + MIN_LENGTH + " characters long");
        }
    }

    public static boolean isValid(String password) {
        try {
            validate(password);
            return true;
        } catch (BadRequestException e) {
            return false;
        }
    }
}
