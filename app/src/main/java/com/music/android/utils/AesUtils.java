package com.music.android.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by yun.liu@avazu.net on 16/4/12.
 */
public class AesUtils {

    /**
     * 加密MODE
     */
    private static final int ENCRYPT = Cipher.ENCRYPT_MODE;

    /**
     * 解密MODE
     */
    private static final int DECRYPT = Cipher.DECRYPT_MODE;

    /**
     * 固定key
     */
    private static final String SALT_KEY = "8vA6au9Z";



    /**
     * MD5加密
     *
     * @param strSrc
     * @param key
     * @return byte[]
     */
    private static byte[] MD5Encode(String strSrc, String key) {
        MessageDigest md5;
        byte[] temp = new byte[0];
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes("UTF8"));
            temp = md5.digest(key.getBytes("UTF8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 数据加密
     *
     * @param str
     * @param key
     * @param mode
     * @return byte[]
     */
    private static byte[] crypt(byte[] str, byte[] key, int mode) {
        final byte[] iv = new byte[16];
        Arrays.fill(iv, (byte) 0x00);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            assert cipher != null;
            cipher.init(mode, skeySpec, ivParameterSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] dstBuff = new byte[0];
        try {
            dstBuff = cipher.doFinal(str);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return dstBuff;
    }

    /**
     * base加密
     *
     * @param bytes
     * @return String
     */
    private static String base64Encode(byte[] bytes) {
        try {
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 截取返回结果的前8位做为动态key
     *
     * @param body
     * @return String
     */
    private static String getBodyDynamicKey(String body) {
        return body.substring(0, 8);
    }

    /**
     * 截取返回结果8位以后的字符串
     *
     * @param body
     * @return String
     */
    private static String getData(String body) {
        int dataLength = body.length();
        return body.substring(8, dataLength);
    }

    /**
     * base64解密
     *
     * @param str
     * @return byte[]
     */
    private static byte[] base64Decode(String str) {
        try {
            return Base64.decode(str, Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * aes加密
     *
     * @param str
     * @param key
     * @return byte[]
     */
    private static byte[] decrypt(byte[] str, byte[] key) {
        try {
            return crypt(str, key, DECRYPT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * aes解密
     *
     * @param str
     * @param key
     * @return byte[]
     */
    private static byte[] encrypt(byte[] str, byte[] key) {
        try {
            return crypt(str, key, ENCRYPT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 加密后的body
     *
     * @param body
     * @return String
     */
    public static String getEncryptBody(String body) {
        try {
            String dynamicKey = getDynamicKey();
            return dynamicKey + base64Encode(encrypt(body.getBytes(), MD5Encode(dynamicKey + SALT_KEY, "")));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 请求的结果
     *
     * @param body
     * @return String
     */
    public static String getDecryptBody(String body) {
        try {
            return new String(decrypt(base64Decode(getData(body)), MD5Encode(getBodyDynamicKey(body) + SALT_KEY, "")));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据生成的6位数进行base64加密生成动态Key
     *
     * @return String
     */
    private static String getDynamicKey() {
        try {
            return base64Encode(String.valueOf(random()).getBytes());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 随机生成一个6位的数值
     *
     * @return int
     */
    private static int random() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random rand = new Random();
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < 6; i++)
            result = result * 10 + array[i];
        return result;
    }
}
