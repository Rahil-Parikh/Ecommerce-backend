package com.hub.ecommerce.service.auth;

import com.hub.ecommerce.exceptions.auth.InvalidAuthenticationProperty;
import com.hub.ecommerce.models.auth.entities.MyUser;

import com.hub.ecommerce.models.auth.models.Register;
import com.hub.ecommerce.models.auth.entities.MyUserAuth;
import com.hub.ecommerce.models.auth.entities.MyUserInformation;
import com.hub.ecommerce.models.auth.models.Address;
import com.hub.ecommerce.repository.auth.MyUserAuthRepository;
import com.hub.ecommerce.repository.auth.MyUserInformationRepository;

import com.hub.ecommerce.repository.auth.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class RegisterService {
    @Autowired
    private MyUserInformationRepository myUserInformationRepository;
    @Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private MyUserAuthRepository myUserAuthRepository;

    public String addToRegistered(Register register) throws  InvalidAuthenticationProperty {
        String otp="";
        if(checkIfUniqueUserName(register.getUsername()) && checkIfUniqueEmailId(register.getEmailId())){
            MyUser myUser = new MyUser(register.getUsername(),register.getPassword(),register.getEmailId(), false,"ROLE_USER", register.getFirstName(),register.getLastName());
            Address address = new Address( register.getAddressLine1(),register.getAddressLine2(),register.getPinCode(),register.getCity(),register.getCountry(),register.getRegion());
            MyUserInformation myUserInformation = new MyUserInformation(myUser,address,register.getPhoneNumber());
            otp = generateOTP(myUser);
            myUserInformationRepository.save(myUserInformation);

        }
        else throw new InvalidAuthenticationProperty("User with similar details already exists");
        return otp;
    }
//    public String addToRegistered(Register register) throws  InvalidAuthenticationProperty {
//        register.setRoles("USER_ROLE");
//        register.setActive(false);
//        if(checkIfUniqueUserName(register.getUserName()) && checkIfUniqueEmailId(register.getEmailId()))
//            userRepository.save(register);
//        else throw new InvalidAuthenticationProperty("User with similar details already exists");
//        return generateOtp(6);
//    }
    public String generateOTP(MyUser myUser){
        String otp=generateOtp(6);
        MyUserAuth myUserAuth = new MyUserAuth();
        myUserAuth.setMyUser(myUser);
        myUserAuth.setOtp(otp);
        myUserAuthRepository.save(myUserAuth);
        return otp;
    }

    public String generateOtp(int len){
        String numbers = "0123456789";

        // Using random method
        Random randomMethod = new Random();

        char[] otp = new char[len];

        for (int i = 0; i < len; i++)
        {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            otp[i] = numbers.charAt(randomMethod.nextInt(numbers.length()));
        }
        return new String(otp);
    }

    public boolean confirmEmailId(String username,String otp) throws  InvalidAuthenticationProperty {
        Optional<MyUser> user = myUserRepository.findByUsername(username);
        if (user.isPresent()) {
            String s = user.get().getMyUserAuth().getOtp();
            System.out.println(otp+"!!"+s);
            if(user.get().getMyUserAuth().getOtp().equals(otp)){
                user.get().setActive(true);
                myUserRepository.save(user.get());
            }
            else {
                throw new InvalidAuthenticationProperty("OTP did not match");
            }
        } else {
            throw new InvalidAuthenticationProperty("Invalid User");
        }
        return true;
    }

    public boolean checkIfUniqueUserName(String userName) {
        Optional<MyUser> user = myUserRepository.findByUsername(userName);
        if (user.isPresent()) return false;
        return true;
    }

    public boolean checkIfUniqueEmailId(String emailId) {
        Optional<MyUser> user = myUserRepository.findByEmailId(emailId);
        if (user.isPresent()) return false;
        return true;
    }
}
