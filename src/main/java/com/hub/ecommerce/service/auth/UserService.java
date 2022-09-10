package com.hub.ecommerce.service.auth;

import com.hub.ecommerce.models.auth.models.MyUserDetails;
import com.hub.ecommerce.models.auth.entities.MyUser;
import com.hub.ecommerce.models.response.MyProfileResponse;
import com.hub.ecommerce.repository.auth.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    MyUserRepository myUserRepository;

    @Override
    public MyUserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<MyUser> user = myUserRepository.findByUsername(userName);
        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + userName));
        return user.map(MyUserDetails::new).get();
    }

    public MyUser loadMyUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<MyUser> user = myUserRepository.findByUsername(userName);
        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + userName));
        return user.get();
    }

    public void updateMyUserByUsername(String userName, MyProfileResponse myProfile) throws UsernameNotFoundException {
        Optional<MyUser> user = myUserRepository.findByUsername(userName);
        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + userName));
        user.get().setFirstName(myProfile.getFirstName());
        user.get().setLastName(myProfile.getLastName());
        user.get().getMyUserInformation().setPhoneNumber(myProfile.getPhoneNumber());
        user.get().getMyUserInformation().setAddress(myProfile.getAddress());
        myUserRepository.save(user.get());
    }
}
