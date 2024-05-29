package com.tec.server.authserver.controller;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.tec.server.authserver.entity.AuthRequest;
import com.tec.server.authserver.entity.UserInfo;
import com.tec.server.authserver.exception.UserNotFoundException;
import com.tec.server.authserver.service.JwtService;
import com.tec.server.authserver.service.UserInfoService;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"}, maxAge = 3600, allowCredentials = "true")
//@CrossOrigin
@RequestMapping("/auth")
public class UserController {

  @Autowired
  private UserInfoService service;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @PostMapping("/addNewUser")
  // @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPERUSER')")
  public ResponseEntity<Object> addNewUser(@RequestBody UserInfo userInfo) {
    return service.addUser(userInfo);
  }

  @CrossOrigin(allowedHeaders = { "Requestor-Type", "Authorization" }, exposedHeaders = "X-Get-Header")
  @GetMapping("/user/getAllUsers")
  public List<UserInfo> getAllUsers() {
    return service.getAllUsers();
  }

  @PutMapping("/user/updateUser")
  // @PreAuthorize("hasAuthority('ROLE_USER')")
  public ResponseEntity<Object> updateUser(@RequestBody UserInfo userInfo) {
    //adding log statement to ouput the user info
    Logger logger = Logger.getLogger(UserController.class.getName());
    logger.info(null != userInfo ? userInfo.toString() : "User info is null");
    return service.updateUser(userInfo);
  }

  @PutMapping("/user/deleteUser")
  // @PreAuthorize("hasAuthority('ROLE_USER')")
  public ResponseEntity<Object> deleteUser(@RequestBody UserInfo userInfo) {
    return service.deleteUser(userInfo);
  }

  @GetMapping("/user/userProfile")
  // @PreAuthorize("hasAuthority('ROLE_USER')")
  public String userProfile() {
    return "{ \"message: \": \"Welcome to User Profile\" }";
  }

  @GetMapping("/admin/adminProfile")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public String adminProfile() {
    return "{ \"message\": \"Welcome to Admin Profile\"}";
  }

  @PostMapping("/login")
  public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
    if (authentication.isAuthenticated()) {
      return "{ \"token\": \"" + jwtService.generateToken(authRequest.getUsername()) + "\"}";
    } else {
      throw new UsernameNotFoundException("invalid user request !");
    }
  }

}
