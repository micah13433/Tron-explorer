package com.tron.explorer.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.tron.explorer.util.StringUtils;

public class AuthInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation ai) {
		HttpSession session = ai.getController().getSession();
		HttpServletRequest request = ai.getController().getRequest();
		
		String redirctUrl = "/wallet/login";
		if(!StringUtils.isBlank(request.getQueryString()) && request.getQueryString().contains("toAddress")){
			redirctUrl = redirctUrl +  ("?returnUrl=" + request.getScheme() +"://" + request.getServerName()+ ":" + request.getServerPort() + "/wallet#/send?" + request.getQueryString());
		}
		if (session == null) {
			ai.getController().redirect(redirctUrl);
		} else {
			String address = (String) session.getAttribute("address");
			if (address != null) {
				ai.invoke();
			} else {
				ai.getController().redirect(redirctUrl);
			}
		}

	}
}