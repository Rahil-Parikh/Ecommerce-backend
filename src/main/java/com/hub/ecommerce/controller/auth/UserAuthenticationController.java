package com.hub.ecommerce.controller.auth;

import com.hub.ecommerce.exceptions.auth.InvalidAuthenticationProperty;
import com.hub.ecommerce.filters.JwtRequestFilter;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.auth.models.MyUserDetails;
import com.hub.ecommerce.models.auth.models.Register;
import com.hub.ecommerce.models.auth.response.RaisedExceptionResponse;
import com.hub.ecommerce.models.auth.response.RestResponseStatus;
import com.hub.ecommerce.service.auth.JwtUtilService;
import com.hub.ecommerce.service.MailService;
import com.hub.ecommerce.service.auth.RegisterService;
import com.hub.ecommerce.service.auth.UserService;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtilService jwtTokenUtil;

    @Autowired
    private UserService userDetailsService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private MailService mailService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody MyUser authenticationRequest,HttpServletResponse response) {
        try {
            System.out.println("-------------------------------Login-----------------------------");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
            System.out.println("-------------------------------LoginOver-----------------------------");
        }
        catch (BadCredentialsException e) {
            System.out.println("Incorrect Username or Password : "+ e.toString());
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse("Incorrect username or password"));
        }
        catch(DisabledException e) {
            System.out.println("Confirm Your Email Id : "+ e.toString());
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse("Please confirm your email-id before signing in."));
        }
        catch(Exception e) {
            System.out.println("Unhandled Exceptions : "+ e.toString());
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse("Something went wrong. Please retry."));
        }

        final MyUserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        Cookie cookie1 = new Cookie("header.payload", jwt.substring(0,jwt.lastIndexOf(".")));
        cookie1.setMaxAge(14*24*60*60);
        cookie1.setSecure(true);
        cookie1.setHttpOnly(true);
        response.addCookie(cookie1);

        Cookie cookie2 = new Cookie("signature", jwt.substring(jwt.lastIndexOf(".")+1));
        cookie2.setMaxAge(14 * 24 * 60 * 60); // expires in 7 days
        cookie2.setSecure(true);
        cookie2.setHttpOnly(false);
        response.addCookie(cookie2);
        return ResponseEntity.ok(new RestResponseStatus("Authentication Successful"));
    }

    @RequestMapping(value = "/deleteAuthTokens", method = RequestMethod.POST)
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response = JwtRequestFilter.deleteCookies(response);
        return ResponseEntity.ok(new RestResponseStatus("Successfully Logged Out"));
    }

//    @GetMapping("/change-username")
//    public String setCookie(HttpServletResponse response) {
//        // create a cookie
//        Cookie cookie = new Cookie("username", "Jovan");
//        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
//        cookie.setSecure(true);
//        cookie.setHttpOnly(false);
//        response.addCookie(cookie);
//        return "Username is changed!";
//    }

    @RequestMapping(value = "/register", method = RequestMethod.POST,consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> register(@RequestBody Register register,HttpServletRequest httpServletRequest) throws Exception {
        try {
            System.out.println("-------------------------------Register-----------------------------");
            String otp =  registerService.addToRegistered(register);
            String baseUrl = String.format("https://website:5001/confirmEmailId/%s/%s",register.getUsername(),otp);

            mailService.sendmail(register.getEmailId(),"Confirm With this OTP", String.format("<h1>Click <a href='%s'>here</a> to validate your account </h1> Ignore if you did not intend to register on Trepechy Jewels",baseUrl));
            System.out.println("-------------------------------Registered-----------------------------");
        }
        catch (InvalidAuthenticationProperty invalidAuthenticationProperty) {
            System.out.println("Exception : "+invalidAuthenticationProperty);
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(invalidAuthenticationProperty.getMessage()));
        }
        RestResponseStatus restResponseStatus = new RestResponseStatus(String.format("OTP Sent to %s",register.getEmailId()));
        return ResponseEntity.ok(restResponseStatus);
    }

    @GetMapping("/confirmEmailId/{username}/{otp}")
    public ResponseEntity<?> confirmEmailId(@PathVariable String username, @PathVariable String otp, HttpServletRequest request) throws InvalidAuthenticationProperty {
        System.out.println(username+"/"+otp);
        try{
            if(registerService.confirmEmailId(username,otp)) {
                RestResponseStatus restResponseStatus = new RestResponseStatus("Account has been verified");
                return ResponseEntity.ok(restResponseStatus);
            }
        }catch (InvalidAuthenticationProperty invalidAuthenticationProperty){
            System.out.println("Exception : "+invalidAuthenticationProperty);
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse(invalidAuthenticationProperty.getMessage()));
        }
        catch (Exception e){
            System.out.println("Unhandled Exceptions : "+ e.toString());
            return ResponseEntity.badRequest().body(new RaisedExceptionResponse("Something went wrong. Please retry."));
        }
        RestResponseStatus restResponseStatus = new RestResponseStatus("Account is invalid");
        return ResponseEntity.ok(restResponseStatus);
    }

    @GetMapping("/mailMe")
    public String mailMe() throws MessagingException {
        mailService.sendmail("email@gmail.com","Hello World","<h1>Test Mail<h1>");
        return ("<h1>Mail Sent Successfully</h1>");
    }

}
