package org.coursera.cybersecurity.courmessenger.service;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {

    private static final int MASTER_SECRET_MIN_LENGTH = 20;

    private static final String PBE_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int PBE_ITERATIONS_COUNT = 1800;

    private static final String SECRET_KEY_ALGORITHM = "AES";
    private static final int SECRET_KEY_LENGTH = 256;

    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int GCM_IV_LENGTH = 12; // in bytes
    private static final int GCM_TAG_LENGTH = 16; // in bytes

    private SecureRandom random = new SecureRandom();

    // one key for each field (we cannot use the same iv+key twice)
    private SecretKey subjectKey;
    private SecretKey bodyKey;

    public CryptoService(@Value("${app.masterSecret}") String masterSecret) throws GeneralSecurityException {

        if (masterSecret.length() < MASTER_SECRET_MIN_LENGTH) {
            throw new ValidationException("Use at least " + MASTER_SECRET_MIN_LENGTH + " chars in 'app.masterSecret'");
        }

        subjectKey = deriveSecretKey(masterSecret, "SUBJECT");
        bodyKey = deriveSecretKey(masterSecret, "BODY");
    }

    public SecretKey getBodyKey() {
        return bodyKey;
    }

    public SecretKey getSubjectKey() {
        return subjectKey;
    }

    public byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        random.nextBytes(iv);
        return iv;
    }

    /**
     * Generate a secretKey based on the password + salt
     */
    private SecretKey deriveSecretKey(String password, String salt) throws GeneralSecurityException {

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PBE_ALGORITHM);
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), PBE_ITERATIONS_COUNT,
                SECRET_KEY_LENGTH);
        SecretKey key = secretKeyFactory.generateSecret(keySpec);

        return new SecretKeySpec(key.getEncoded(), SECRET_KEY_ALGORITHM);
    }

    public byte[] encryptData(byte[] iv, SecretKey secretKey, byte[] plainText) throws GeneralSecurityException {

        byte[] cipherText = null;

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);

        // params
        GCMParameterSpec params = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        // init
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, params);

        // encrypt
        cipherText = cipher.doFinal(plainText);

        return cipherText;
    }

    public byte[] decryptData(byte[] iv, SecretKey secretKey, byte[] cipherText) throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);

        // params
        GCMParameterSpec params = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        // init
        cipher.init(Cipher.DECRYPT_MODE, secretKey, params);

        // decrypt
        byte[] plainText = cipher.doFinal(cipherText);

        return plainText;
    }

}
