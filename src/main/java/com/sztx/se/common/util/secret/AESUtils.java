/**
 * 
 */
package com.sztx.se.common.util.secret;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sztx.se.common.exception.SystemException;

/**
 * aes加密算法
 * 
 * @author zhihongp
 * 
 */
public class AESUtils {

	private static final Logger LOG = LoggerFactory.getLogger(AESUtils.class);

	/**
	 * 加密
	 * 
	 * @param content 需要加密的内容
	 * @param password 加密密码
	 * @return
	 */
	public static String encrypt(String content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");

			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(password.getBytes());

			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);

			return URLEncoder.encode(Base64.encodeBase64String(result), "utf-8");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new SystemException("encrypt error", e);
		}
	}

	/**
	 * 解密
	 * 
	 * @param content 待解密内容
	 * @param password 解密密钥
	 * @return
	 */
	public static String decrypt(String content, String password) {
		try {

			byte[] contentBytes = Base64.decodeBase64(URLDecoder.decode(content, "utf-8"));
			KeyGenerator kgen = KeyGenerator.getInstance("AES");

			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(password.getBytes());

			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(contentBytes);

			return new String(result, "utf-8");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new SystemException("decrypt error", e);
		}
	}
}
