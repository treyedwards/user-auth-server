package com.tec.server.authserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tec.server.authserver.controller.UserController;
import com.tec.server.authserver.entity.UserInfo;
import com.tec.server.authserver.exception.DuplicateUserException;
import com.tec.server.authserver.exception.PermissionDeniedException;
import com.tec.server.authserver.exception.UserNotFoundException;
import com.tec.server.authserver.repository.UserInfoRepository;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserInfoService implements UserDetailsService {

    private static final String SUADMIN_USERNAME = "suadmin";
    private static final String SUADMIN_EMAIL = "suadmin@suadmin.com";

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserInfo> userDetail = repository.findByName(username);

        // Converting userDetail to UserDetails
        // or else throw exception
        // in map() we're removing the password from the userDetail
        return userDetail
                .map(user -> {
                    // Create a copy of the user and clear the password
                    // user.setPassword(null); // or user.setPassword("");
                    return new UserInfoDetails(user);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    public ResponseEntity<Object> addUser(UserInfo userInfo) {
        Optional<UserInfo> opt = repository.findByName(userInfo.getName());
        if (opt.isPresent()) {
            throw new DuplicateUserException(String.format("User name [%s] already exists!", userInfo.getName()));
        }
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return ResponseEntity.created(null).build();

    }

    public ResponseEntity<Object> updateUser(UserInfo userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        Optional<UserInfo> updateUser = repository.findById(userInfo.getId());
        if (updateUser.isPresent()) {
            String suName = updateUser.get().getName();
            String newName = userInfo.getName();
            if (suName.equals(UserInfoService.SUADMIN_USERNAME)
                    && !newName.equals(UserInfoService.SUADMIN_USERNAME)) {
                throw new PermissionDeniedException("ERROR: suadmin user-name can not be changed!");
            }

            String suEmail = updateUser.get().getEmail();
            String newEmail = userInfo.getEmail();
            if (suEmail.equals(UserInfoService.SUADMIN_EMAIL)
                    && !newEmail.equals(UserInfoService.SUADMIN_EMAIL)) {
                throw new PermissionDeniedException("ERROR: suadmin email can not be changed!");
            }

            updateUser.get().setEmail(userInfo.getEmail());
            updateUser.get().setName(userInfo.getName());
            updateUser.get().setFirstName(userInfo.getFirstName());
            updateUser.get().setLastName(userInfo.getLastName());
            updateUser.get().setPassword(userInfo.getPassword());
            Logger logger = Logger.getLogger(UserInfoService.class.getName());
            logger.info("Calling user info service to update user info");
            logger.info(null != userInfo ? userInfo.toString() : "User info is null");
            updateUser.get().setRoles(userInfo.getRoles());
            repository.save(updateUser.get());
        } else {
            throw new UserNotFoundException(String.format("User ID(%d) Not Found", userInfo.getId()));
        }
        return ResponseEntity.ok(updateUser);

    }

    public ResponseEntity<Object> deleteUser(UserInfo userInfo) {

        Optional<UserInfo> opt = this.repository.findById(((long) userInfo.getId()));
        if (opt.isEmpty()) {
            throw new UserNotFoundException(String.format("User ID(%d) Not Found", userInfo.getId()));
        }
        String suName = opt.get().getName();
        String newName = userInfo.getName();
        if (suName.equals(UserInfoService.SUADMIN_USERNAME)
                && !newName.equals(UserInfoService.SUADMIN_USERNAME)) {
            throw new PermissionDeniedException("ERROR: suadmin user can not be deleted!");
        }
        repository.delete(userInfo);
        return ResponseEntity.ok(userInfo);
    }

    // should remove encrypted password before returning
    public List<UserInfo> getAllUsers() {
        List<UserInfo> users = repository.findAll();
        users.forEach(user -> user.setPassword(null)); // or user.setPassword("");
        return users;
    }
}
