package com.fictio.parrot.demo;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AESUtils {

    private static final String DEFAULT_KEYSEED = "@mangkhut#";

    private static final String ERR_MSG = "Nonce size is incorrect,Make sure that the incoming data is an AES-GCM encrypted";

    // AES-GCM parameters
    public static final int AES_KEY_SIZE = 128; // in bits
    public static final int GCM_NONCE_LENGTH = 12; // in bytes
    public static final int GCM_TAG_LENGTH = 16; // in bytes

    public static String gcmEncrypt(String content) {
        try {
            SecureRandom sr = new SecureRandom();
            final byte[] iv = new byte[GCM_NONCE_LENGTH];
            sr.nextBytes(iv);
            SecretKey key = getKey(DEFAULT_KEYSEED);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] cipherText = cipher.doFinal(content.getBytes("UTF-8"));
            ByteBuffer bf = ByteBuffer.allocate(4 + iv.length + cipherText.length);
            bf.putInt(iv.length);
            bf.put(iv);
            bf.put(cipherText);
            byte[] cipherResult = bf.array();
            log.debug("cihperText: {}",cipherResult);
            return encodeToString(cipherResult);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
                BadPaddingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String gcmDecrypt(String content) {
        try {
            byte[] input = decodeFromString(content);
            log.debug("decryptText: {}", input);
            ByteBuffer byteBuffer = ByteBuffer.wrap(input);
            int noonceSize = byteBuffer.getInt();
            if(noonceSize < GCM_NONCE_LENGTH || noonceSize >= GCM_TAG_LENGTH) throw new IllegalArgumentException(ERR_MSG);
            final byte[] iv = new byte[noonceSize];
            byteBuffer.get(iv);
            byte[] cipherBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherBytes);

            SecretKey key = getKey(DEFAULT_KEYSEED);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);

            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] cipherText = cipher.doFinal(cipherBytes);
            return new String(cipherText,"UTF-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException |
                UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    private static SecretKey getKey(String seed, byte[] iv)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if(seed == null || seed.isEmpty()) seed = DEFAULT_KEYSEED;
        KeySpec spec = new PBEKeySpec(seed.toCharArray(), iv, 65536, 128);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] key = keyFactory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, "AES");
    }

    private static SecretKey getKey (String seed) {
        if(seed == null || seed.isEmpty()) seed = DEFAULT_KEYSEED;
        ByteBuffer tagBuffer = ByteBuffer.allocate(GCM_TAG_LENGTH);
        tagBuffer.put(seed.getBytes());
        return new SecretKeySpec(tagBuffer.array(), "AES");
    }

    private static String encodeToString(byte[] input) {
        return Base64.encodeBase64String(input);
    }

    private static byte[] decodeFromString(String input) {
        return Base64.decodeBase64(input);
    }

    @Test
    public void test() {
        String unEncryptStr = "32048319880206262X";
        String encryptStr = gcmEncrypt(unEncryptStr);
        log.info("encryptStr : {}",encryptStr);
        log.info("decryptStr: {}", gcmDecrypt(encryptStr));
        encryptStr = "AAAADBJqo9sHP8g1XceznNiFDPCDsB3J8wCDpjioKcpijx8bA5ohZnZhqA==";
        log.info("decryptStr: {}", gcmDecrypt(encryptStr));
    }

}
