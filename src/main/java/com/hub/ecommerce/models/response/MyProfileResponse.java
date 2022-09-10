package com.hub.ecommerce.models.response;

import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.auth.entities.MyUserAuth;
import com.hub.ecommerce.models.auth.entities.MyUserInformation;
import com.hub.ecommerce.models.auth.models.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MyProfileResponse implements Serializable {

    private String username;
    private String emailId;
    private String roles;
    private String firstName;
    private String lastName;

    private Address address;
    private String phoneNumber;


    public MyProfileResponse(MyUser myUser){

        this.username = myUser.getUsername();
        this.emailId = myUser.getEmailId();
        this.roles = myUser.getRoles();
        this.firstName = myUser.getFirstName();
        this.lastName =myUser.getLastName();
        this.address = myUser.getMyUserInformation().getAddress();
        this.phoneNumber = myUser.getMyUserInformation().getPhoneNumber();
    }

    public MyProfileResponse(String username, String emailId, String firstName, String lastName, Address address, String phoneNumber) {
        this.username = username;
        this.emailId = emailId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
