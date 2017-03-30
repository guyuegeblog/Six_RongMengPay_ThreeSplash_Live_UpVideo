package com.app.Tool;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesTool {

    //渤海同步
    public static String masterPassword = "123456abcdefg";

    public static String encrypt(String cleartext) {
        try {
            cleartext = URLEncoder.encode(cleartext, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] rawKey = new byte[0];
        try {
            rawKey = getRawKey(masterPassword.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] result = new byte[0];
        try {
            result = encrypt(rawKey, cleartext.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toHex(result);
    }

    public static String decrypt(String encrypted) {
        String result_Text = "";
        try {
            byte[] rawKey = new byte[0];
            rawKey = getRawKey(masterPassword.getBytes());
            byte[] enc = toByte(encrypted);
            byte[] result = new byte[0];
            result = decrypt(rawKey, enc);
            result_Text = URLDecoder.decode(new String(result), "UTF-8");
        }catch (Exception E){
            result_Text = "";
        }
        return result_Text;
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
//		KeyGenerator kgen = KeyGenerator.getInstance("AES");
//		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//		sr.setSeed(seed);
//		kgen.init(128, sr); // 192 and 256 bits may not be available
//		SecretKey skey = kgen.generateKey();
//		byte[] raw = skey.getEncoded();
//		return raw;
        return toByte("80D1626A9345CBEFC21C5B68EF136B60");
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        System.out.println(buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

//    public static void main(String[] args) {
//
//        //为加密和解密的密码
//        String masterPassword = "123456abcdefg";
//        //加密和解密的内容
//        String originalText1 = "1";
//        String originalText2 = "heheda";
//        String originalText3 = "999";
//        String originalText4 = "哈10";
//        String originalText5 = "欧美";
//        String originalText6 = "";
//        try {
//            //加密的方法：encrypt。传入密码和加密内容 ，返回String 为加密后密文
//            String encryptingCode1 = encrypt(masterPassword,
//                    originalText1);
//            String encryptingCode2 = encrypt(masterPassword,
//                    originalText2);
//            String encryptingCode3 = encrypt(masterPassword,
//                    originalText3);
//            String encryptingCode4 = encrypt(masterPassword,
//                    originalText4);
//            String encryptingCode5 = encrypt(masterPassword,
//                    originalText5);
//            String encryptingCode6 = encrypt(masterPassword,
//                    originalText6);
//            System.err.println("加密结果111为 " + encryptingCode1);
//            System.err.println("加密结果222为 " + encryptingCode2);
//            System.err.println("加密结果333为 " + encryptingCode3);
//            System.err.println("加密结果444为 " + encryptingCode4);
//            System.err.println("加密结果555为 " + encryptingCode5);
//            System.err.println("加密结果666为 " + encryptingCode6);
//            //解密的方法：decrypt。传入密码和需要解密的密文，返回String 为解密后的原文
//            String decryptingCode = decrypt(masterPassword, "75CAC5AF625B8114C08557FD67EC4FE3");
//            decryptingCode = URLDecoder.decode(decryptingCode, "UTF-8");
//            System.err.println("解密结果为 " + decryptingCode);
//            System.err.println(URLEncoder.encode(""));
//            System.err.println(URLDecoder.decode("weixin://wap/pay?appid%3Dwx3745712c85e2ac4d%26noncestr%3D50421606291013149274%26package%3DWAP%26prepayid%3Dwx201606291013143e58ed13230336371961%26sign%3DEB291A7CE5AEAE7138737A4A75C594FE%26timestamp%3D1467166394"));
//        } catch (Exception e) {
//            System.err.println("" + e.toString());
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

}
