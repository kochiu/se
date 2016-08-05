package com.kochiu.se.web.controller;

import java.security.Key;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kochiu.se.common.domain.ResultCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kochiu.se.common.domain.Result;
import com.kochiu.se.common.util.secret.RSAUtil;
import com.kochiu.se.web.filter.EncryptFilter;

@Controller
public class EncryptController extends BaseController {

	@RequestMapping(value = "/getPublicKey")
	public Result getPublicKey(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Map<String, Key> keyMap = RSAUtil.initKeys();
		String publicKey = RSAUtil.getPublicKey(keyMap);
		String privateKey = RSAUtil.getPrivateKey(keyMap);
		HttpSession session = request.getSession();
		session.setAttribute(EncryptFilter.PUBLIC_KEY, publicKey);
		session.setAttribute(EncryptFilter.PRIVATE_KEY, privateKey);
		// 返回页面
		Result result = new Result(ResultCode.COMMON_SUCCESS, true);
		result.setProperty("publicKey", publicKey);
		return result;
	}

	@RequestMapping(value = "/exchangeSecretKey")
	public Result testRedis(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String data = getStringParameter("secretKey");
		HttpSession session = request.getSession();
		String key = (String) session.getAttribute(EncryptFilter.PRIVATE_KEY);
		String secretKey = null;

		if (data != null && key != null) {
			secretKey = RSAUtil.decryptByPrivateKeyStr(data, key);
		}
		
		boolean flag = false;
		
		if (secretKey != null) {
			flag = true;
			session.setAttribute(EncryptFilter.SECRET_KEY, secretKey);
		}

		// 返回页面
		Result result = new Result(ResultCode.COMMON_SUCCESS, flag);
		return result;
	}

}
