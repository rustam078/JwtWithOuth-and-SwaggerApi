package com.abc.services;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordGeneratorService {

    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "@#$&";
    private static final String ALL_CHARACTERS = LOWER_CASE + UPPER_CASE + DIGITS + SPECIAL_CHARACTERS;

    private final SecureRandom random = new SecureRandom();

    public String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length should be at least 8 characters");
        }
        if (length > 40) {
            throw new IllegalArgumentException("Password length should be at most 40 characters");
        }

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALL_CHARACTERS.length());
            password.append(ALL_CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}