package com.study.etc;

import com.study.etc.signature.SignatureUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class SignatureTest {
    private static KeyPair keyPair;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    @BeforeAll
    static void setUp() throws NoSuchAlgorithmException, NoSuchProviderException {
        keyPair = SignatureUtil.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    @Test
    void testSignAndVerify() throws Exception {
        String data = "testSignAndVerify";
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

        // 전자서명 생성
        byte[] signature = SignatureUtil.signData(dataBytes, privateKey);

        // 전자서명 검증
        boolean isVerified = SignatureUtil.verifySignature(dataBytes, signature, publicKey);

        log.info("Signature: {}", Base64.getEncoder().encodeToString(signature));
        log.info("Signature is valid: {}", isVerified);

        assertThat(signature).isNotNull();
        assertTrue(isVerified, "The signature should be valid");
    }
}
