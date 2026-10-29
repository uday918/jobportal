package com.jobportal;

import static org.junit.jupiter.api.Assertions.*;

import com.jobportal.security.AesGcmEncryptor;
import org.junit.jupiter.api.Test;

class AesGcmEncryptorTest {

    private static final String KEY = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @Test
    void encryptDecryptRoundTrip() {
        AesGcmEncryptor enc = new AesGcmEncryptor(KEY);
        String cipher = enc.encrypt("+91 7986933728");
        assertNotEquals("+91 7986933728", cipher);
        assertEquals("+91 7986933728", enc.decrypt(cipher));
    }

    @Test
    void sameInputGivesDifferentCipherText() {
        AesGcmEncryptor enc = new AesGcmEncryptor(KEY);
        assertNotEquals(enc.encrypt("hello"), enc.encrypt("hello"));
    }

    @Test
    void tamperedPayloadIsRejected() {
        AesGcmEncryptor enc = new AesGcmEncryptor(KEY);
        String cipher = enc.encrypt("hello");
        byte[] raw = java.util.Base64.getDecoder().decode(cipher);
        raw[raw.length - 1] ^= 0x01; // flip one bit in the auth tag
        String tampered = java.util.Base64.getEncoder().encodeToString(raw);
        assertThrows(IllegalArgumentException.class, () -> enc.decrypt(tampered));
    }

    @Test
    void keyMustBe32Bytes() {
        assertThrows(IllegalArgumentException.class, () -> new AesGcmEncryptor("c2hvcnQ="));
    }
}
