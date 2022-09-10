package com.hub.ecommerce.application;

import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.auth.entities.MyUserInformation;
import com.hub.ecommerce.models.auth.models.Address;
import com.hub.ecommerce.repository.auth.MyUserInformationRepository;
import com.hub.ecommerce.repository.auth.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
class JpaApplicationRunner implements ApplicationRunner {
    @Autowired
    MyUserRepository myUserRepository;
    @Autowired
    MyUserInformationRepository myUserInformationRepository;

    @Override
    public void run(ApplicationArguments args) {
        MyUser myUser = new MyUser("admin","adminpass","randomAdmin@email.com", true,"ROLE_ADMIN", "fName","lName");
        Address address = new Address( "Address Line 1","Address Line 2",400004,"Mumbai","India","Maharashtra");
        MyUserInformation  myUserInformation= new MyUserInformation(myUser,address,"1234567890");

        MyUser myUser2 = new MyUser("user","userpass","randomUser@email.com", true,"ROLE_USER", "firstName","lastName");
        Address address2 = new Address( "ALine 1","ALine 2",400024,"NY","USA","New York");
        MyUserInformation myUserInformation2 = new MyUserInformation(myUser2,address2,"0123456789");
        myUserInformationRepository.save(myUserInformation);
        myUserInformationRepository.save(myUserInformation2);
    }
}
// Collect Data
// page1 => userName - password - emailId - phoneNumber
// page2 => Address Line 1 - Line 2 - pinCode - city - country
// Send Data
// Mail for Confirmation OTP
// Active -> true