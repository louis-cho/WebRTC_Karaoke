package com.ssafy.server.user.secure;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SHA_512 {
    public static String getSalt() {
        String salt = "";
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");	// SHA1PRNG 난수화 알고리즘
            byte[] bytes = new byte[16];
            random.nextBytes(bytes);	// 16 바이트 난수 생성
            salt = new String(Base64.getEncoder().encode(bytes));	// 바이너리를 문자열로 변환
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return salt;
    }

    public static String SHA512(String plaintext, String salt) {
        String hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512"); // 해시 함수 값을 구하기 위한 MessageDigest
            md.update((plaintext + salt).getBytes()); // 평문과 salt 문자열을 합친 입력값 설정
            hash = String.format("%0128x", new BigInteger(1, md.digest())); // 해시 값 구하기 (10진법 BigInteger를 16진법으로)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static String DSHA512(String plaintext, String salt) { // 이중 해싱 (해시 충돌 시 사용)
        return SHA512(SHA512(plaintext, salt), salt);
    }
}