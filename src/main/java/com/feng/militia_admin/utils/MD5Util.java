package com.feng.militia_admin.utils;

import java.security.MessageDigest;

/**
 * @author FengMeng
 * @version 1.0
 * @date 2026/6/4 20:07
 * @description 类功能描述
 */
public class MD5Util {
    public static String md5(String s) {
        try {
            return String.format("%032x", new java.math.BigInteger(1, MessageDigest.getInstance("MD5").digest(s.getBytes())));
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
