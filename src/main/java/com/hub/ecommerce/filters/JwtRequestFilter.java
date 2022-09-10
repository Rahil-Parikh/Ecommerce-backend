package com.hub.ecommerce.filters;

import com.hub.ecommerce.service.auth.JwtUtilService;
import com.hub.ecommerce.service.auth.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userDetailsService;

    @Autowired
    private JwtUtilService jwtUtil;

    public static HttpServletResponse deleteCookies(HttpServletResponse response) {
        Cookie deleteServletCookie1 = new Cookie("header.payload", null);
        deleteServletCookie1.setMaxAge(0);
        deleteServletCookie1.setSecure(true);
        deleteServletCookie1.setHttpOnly(true);
        response.addCookie(deleteServletCookie1);
        Cookie deleteServletCookie2 = new Cookie("signature", null);
        deleteServletCookie2.setMaxAge(0);
        deleteServletCookie2.setSecure(true);
        deleteServletCookie2.setHttpOnly(false);
        response.addCookie(deleteServletCookie2);
        return response;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        boolean cookiesDeleted = false;

        final Cookie[] cookies = request.getCookies();
        String authorizationToken = "";
//        System.out.println("cookies = "+request.getCookies());
        Cookie header = WebUtils.getCookie(request, "header.payload");
        if (header != null) {
            Cookie signature = WebUtils.getCookie(request, "signature");
            if (signature != null) {
                authorizationToken = header.getValue() + "." + signature.getValue();
            } else {
                response = deleteCookies(response);
                cookiesDeleted = true;
                System.out.println("Cookie Signature not found");
            }
        } else {
            response = deleteCookies(response);
            cookiesDeleted = true;
            System.out.println("Cookies not found : " + request.getMethod() + " "+ request.getRequestURI() + " "+ request.getServletPath()  + " "+ request.getContextPath());
        }


//        if(cookies != null) {
//            if (cookies[0].getName().equals("header.payload")&& cookies[1].getName().equals("signature")) {
//                authorizationToken = cookies[0].getValue() + "." + cookies[1].getValue();
//            } else if (cookies[1].getName().equals("header.payload")&& cookies[0].getName().equals("signature")) {
//                authorizationToken = cookies[1].getValue() + "." + cookies[0].getValue();
//            }else{
////                response = deleteCookies(response);
//                System.out.println("No header and signature"+cookies.length+ " - "+cookies.toString());
//            }
//        }else {
////            response = deleteCookies(response);
//        }
        System.out.println("authtoken " + authorizationToken.length());
        String username = null;

        if (authorizationToken != "") {
            try {
                username = jwtUtil.extractUsername(authorizationToken);
            } catch (ExpiredJwtException expiredJwtException) {
                cookiesDeleted = true;
                response = deleteCookies(response);
            }
        } else {
            if (!cookiesDeleted) {
                response = deleteCookies(response);
                cookiesDeleted = true;
            }
            System.out.println("Authorization Token is empty ");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(authorizationToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                if (!cookiesDeleted) {
                    response = deleteCookies(response);
                }
                System.out.println("Invalid Token");
            }
        } else {
            if (!cookiesDeleted) {
                response = deleteCookies(response);
            }
            System.out.println("username == null or SecurityContextHolder.getContext().getAuthentication() == null");
        }
        chain.doFilter(request, response);
    }

}
