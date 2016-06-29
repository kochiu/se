package com.sztx.se.common.util.secret;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import com.sztx.se.common.exception.SystemException;

/**
 * md5加密
 * 
 * @author zhihongp
 * 
 */
public class MD5Utils {

	private static final String CHARSET = "UTF-8";

	private static final String ALG = "MD5";

	public static String encrypt(byte[] bytes, boolean base64) {
		byte[] digest = null;
		try {
			digest = DigestPass.digest(ALG, bytes);
		} catch (NoSuchAlgorithmException e) {
			throw new SystemException(e);
		}

		if (base64) {
			return base64Encode(digest);
		} else {
			return encode(digest);
		}
	}

	public static String encrypt(String str, boolean base64) {
		byte[] digest = null;
		try {
			digest = DigestPass.digest(ALG, str.getBytes(CHARSET));
		} catch (NoSuchAlgorithmException e) {
			throw new SystemException(e);
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		}

		if (base64) {
			return base64Encode(digest);
		} else {
			return encode(digest);
		}
	}

	public static String encryptWithKey(String str, String key, boolean base64) {
		byte[] digest = null;
		try {
			digest = DigestPass.digest(ALG, str.getBytes(CHARSET), key.getBytes(CHARSET));
		} catch (NoSuchAlgorithmException e) {
			throw new SystemException(e);
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		}

		if (base64) {
			return base64Encode(digest);
		} else {
			return encode(digest);
		}
	}

	public static String encrypt(byte[] bytes) {
		return encrypt(bytes, false);
	}

	public static String encrypt(String str) {
		return encrypt(str, false);
	}

	public static String encryptWithKey(String str, String key) {
		return encryptWithKey(str, key, false);
	}

	private static String encode(byte[] plain) {
		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < plain.length; i++) {
			int val = ((int) plain[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();
	}

	private static String base64Encode(byte[] plain) {
		return new String(Base64.encodeBase64(plain));
	}

	public static void main(String[] args) {
		String s = "123abc";
		String s1 = MD5Utils.encrypt(s);
		String s2 = MD5Utils.encrypt(s, true);
		System.out.println(s);
		System.out.println(s1);
		System.out.println(s2);
	}
}

class DigestPass {
	/**
	 * 
	 * @param alg String 算法，通常为MD5,SHA
	 * @param plainByte byte[] 明文的二进制
	 * @return byte[] 二进制的消息摘要
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] digest(String alg, byte[] plainByte) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(alg);
		md.update(plainByte);
		return md.digest();
	}

	public static byte[] digest(String alg, byte[] plainByte, byte[] key) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(alg);
		md.update(plainByte);
		return md.digest(key);
	}
}
