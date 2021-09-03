package com.java.xuhaotian;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public static String md5(String s) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(s.getBytes());
            byte[] digestBytes = messageDigest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte digestByte : digestBytes) {
                StringBuilder tmp = new StringBuilder(Integer.toHexString(0xFF & digestByte));
                while (tmp.length() < 2) tmp.insert(0, "0");
                hexString.append(tmp);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
