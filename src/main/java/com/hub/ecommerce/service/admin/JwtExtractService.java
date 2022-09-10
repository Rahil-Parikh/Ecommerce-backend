package com.hub.ecommerce.service.admin;

import com.hub.ecommerce.exceptions.customer.InvalidPropertyMyUserId;
import com.hub.ecommerce.service.auth.JwtUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class JwtExtractService {
    @Autowired
    private JwtUtilService jwtUtil;

    public String getUserName(HttpServletRequest httpServletRequest) throws InvalidPropertyMyUserId {
        final Cookie[] cookies= httpServletRequest.getCookies();
        String authorizationToken= "";

        if(cookies != null&&cookies.length==2) {
            if (cookies[0].getName().equals("header.payload")&& cookies[1].getName().equals("signature")) {
                authorizationToken = cookies[0].getValue() + "." + cookies[1].getValue();
            } else if (cookies[1].getName().equals("header.payload")&& cookies[0].getName().equals("signature")) {
                authorizationToken = cookies[1].getValue() + "." + cookies[0].getValue();
            }
        }

        if (authorizationToken == "" ) {
            throw new InvalidPropertyMyUserId("Auth Cheated!");
        }

        return jwtUtil.extractUsername(authorizationToken);
    }
}
