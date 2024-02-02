package com.ssafy.server.user.util;

import com.ssafy.server.user.secure.RSA_2048;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RSAKeyManager {
    private static HashMap<String, KeyPair> keyMap;
    private static HashMap<String, Long> lastRequest;
    private static RSAKeyManager instance = null;

    private RSAKeyManager() {

    }

    public HashMap<String, KeyPair> getKeyMap() {
        return keyMap;
    }

    public HashMap<String, Long> getLastRequest() {
        return lastRequest;
    }

    public static RSAKeyManager getInstnace() {
        if(instance == null) {
            instance = new RSAKeyManager();
            keyMap = new HashMap<>();
            lastRequest = new HashMap<>();
        }

        return instance;
    }

    /**
     * user ip를 unique key 값으로 키 생성 후 공개키를 반환한다
     * @param ip
     * @return 공개키
     */
    public PublicKey createKeyPair(String ip) {
        if(ip == null || !isValidIP(ip))
            return null;

        if(keyMap.get(ip) == null) {
            KeyPair keyPair = RSA_2048.createKey();
            keyMap.put(ip, keyPair);
            lastRequest.put(ip, System.currentTimeMillis());

            return keyPair.getPublic();
        } else {
            return keyMap.get(ip).getPublic();
        }
    }

    public PrivateKey getPrivateKey(String ip) {
        if(ip == null || !isValidIP(ip))
            return null;

        if(keyMap.get(ip) == null) {
            return null;
        }

        lastRequest.put(ip, System.currentTimeMillis());
        return keyMap.get(ip).getPrivate();
    }

    public String convertPrivateKeyToString(PrivateKey privateKey) {
        // PrivateKey를 바이트 배열로 변환
        byte[] privateKeyBytes = privateKey.getEncoded();

        // 바이트 배열을 Base64로 인코딩하여 문자열로 변환
        String privateKeyString = Base64.getEncoder().encodeToString(privateKeyBytes);

        return privateKeyString;

    }


    public PublicKey getPublicKey(String ip) {
        if(ip == null || !isValidIP(ip))
            return null;

        if(keyMap.get(ip) == null) {
            return null;
        }

        lastRequest.put(ip, System.currentTimeMillis());
        return keyMap.get(ip).getPublic();
    }

    public RSAPublicKeySpec getPublicKeySpace(String ip) {
        if(ip == null || !isValidIP(ip))
            return null;

        if(keyMap.get(ip) == null) {
            return null;
        }

        return getPublicKeySpec(((KeyPair)keyMap.get(ip)).getPublic());
    }

    private static RSAPublicKeySpec getPublicKeySpec(PublicKey pk) {
        if (pk == null)
            return null;
        try
        {
            return (RSAPublicKeySpec)KeyFactory.getInstance("RSA").getKeySpec(pk, RSAPublicKeySpec.class);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }return null;
    }


    public String getPublicKeyModulus(String ip) {
        RSAPublicKey pk = (RSAPublicKey) RSAKeyManager.getInstnace().getPublicKey(ip);
        if(pk == null)
            return null;

        return pk.getModulus().toString(16);
    }

    public String getPublicKeyExponent(String ip) {
        RSAPublicKey pk = (RSAPublicKey) RSAKeyManager.getInstnace().getPublicKey(ip);
        if(pk == null)
            return null;
        return pk.getPublicExponent().toString(16);
    }

    public boolean isValidIP(String ip) {
        String regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
		// return matcher.matches();
        return true;
    }


}
