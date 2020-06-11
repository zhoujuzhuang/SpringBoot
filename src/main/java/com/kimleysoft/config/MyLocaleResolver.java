package com.kimleysoft.config;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.LocaleResolver;

public class MyLocaleResolver implements LocaleResolver {

	private static final String LANG = "lang";
    private static final String LANG_SESSION = "lang_session";
    
    /**
     * 响应页面的时候才会执行
     */
	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		String lang = request.getParameter(LANG);
        Locale locale = Locale.getDefault();
        if (lang != null && lang != ""){
            String[] langueage = lang.split("_");
            locale = new Locale(langueage[0],langueage[1]);
 
            HttpSession session = request.getSession();
            session.setAttribute(LANG_SESSION,locale);
        }else{
            HttpSession session = request.getSession();
            Locale localeInSession = (Locale) session.getAttribute(LANG_SESSION);
            if (localeInSession != null){
                locale = localeInSession;
            }
        }
        return locale;
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse respose,
			Locale local) {

	}

}
