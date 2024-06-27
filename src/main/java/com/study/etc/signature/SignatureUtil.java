package com.study.etc.signature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;

public class SignatureUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // 키 쌍 생성
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        // RSA 알고리즘과 Bouncy Castle 보안 제공자를 사용하여 KeyPairGenerator 객체를 생성
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        // 키 크기를 2048 비트로 설정
        keyPairGenerator.initialize(2048);

        return keyPairGenerator.generateKeyPair();
    }

    // 전자서명 생성
    public static byte[] signData(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        // Bouncy Castle 보안 제공자를 사용하여 SHA256withRSA 알고리즘으로 서명 객체 생성
        Signature signature = Signature.getInstance("SHA256withRSA", "BC");
        // 서명 객체 초기화 (서명을 위한 개인 키 설정)
        signature.initSign(privateKey);
        signature.update(data);

        return signature.sign();
    }

    // 전자서명 검증
    public static boolean verifySignature(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA", "BC");
        // 검증을 위한 공개 키 설정
        signature.initVerify(publicKey);
        signature.update(data);

        return signature.verify(signatureBytes);
    }
}
