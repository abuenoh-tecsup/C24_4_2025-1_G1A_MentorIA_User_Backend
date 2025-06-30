package com.tecsup.demo.authentication.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public class DjangoPasswordEncoder implements PasswordEncoder {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int DEFAULT_ITERATIONS = 1000000; // Django 5.x usa 1M iteraciones

    @Override
    public String encode(CharSequence rawPassword) {
        // Retorna un valor dummy para evitar errores si Spring lo llama por seguridad
        return "pbkdf2_sha256$260000$dummy$" + Base64.getEncoder().encodeToString("dummy".getBytes());
    }


    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }

        // Formato Django: pbkdf2_sha256$iterations$salt$hash
        String[] parts = encodedPassword.split("\\$");
        if (parts.length != 4) {
            return false;
        }

        String algorithm = parts[0];
        int iterations;
        String salt = parts[2];
        String expectedHash = parts[3];

        // Verificar que sea el algoritmo correcto
        if (!"pbkdf2_sha256".equals(algorithm)) {
            return false;
        }

        try {
            iterations = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        try {
            // Generar hash con la contraseña proporcionada
            String actualHash = pbkdf2(rawPassword.toString(), salt, iterations);
            return expectedHash.equals(actualHash);
        } catch (Exception e) {
            return false;
        }
    }

    private String pbkdf2(String password, String salt, int iterations)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, iterations, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = keyFactory.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return false; // No necesitamos actualizar encoding
    }
}
