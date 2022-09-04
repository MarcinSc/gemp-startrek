package com.gempukku.startrek.common;

public class UserValidation {
    private static final String validFirstCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String validCharacters = validFirstCharacters + "0123456789 -_";

    public static void validateUser(String username, String email, String password) throws UserValidationException {
        if (username.length() < 3 || username.length() > 15)
            throw new UserValidationException("Username has to have 3-15 characters");
        else if (validFirstCharacters.indexOf(username.toCharArray()[0]) < 0)
            throw new UserValidationException("Username has to start with a latin letter");
        else if (!hasValidCharacters(username))
            throw new UserValidationException("Username can contain letters, numbers, space, - and _");
        else if (!isValidEmail(email)) {
            throw new UserValidationException("Invalid email");
        } else if (password.length() < 8)
            throw new UserValidationException("Password has to have at least 8 characters");
    }

    private static boolean isValidEmail(String email) {
        int splitPosition = email.indexOf('@');

        if (splitPosition < 0) {
            return false;
        }

        String localPart = email.substring(0, splitPosition);
        String domainPart = email.substring(splitPosition + 1);

        if (localPart.length() == 0 || domainPart.length() == 0)
            return false;

        return true;
    }

    private static boolean hasValidCharacters(String username) {
        for (char c : username.toCharArray()) {
            if (validCharacters.indexOf(c) < 0)
                return false;
        }
        return true;
    }

    public static class UserValidationException extends Exception {
        public UserValidationException(String message) {
            super(message);
        }
    }
}
