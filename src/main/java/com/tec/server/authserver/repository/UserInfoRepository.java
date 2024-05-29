package com.tec.server.authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tec.server.authserver.entity.UserInfo;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
  Optional<UserInfo> findByName(String username);
  Optional<UserInfo> findByEmail(String userName);
  Optional<UserInfo> findById(long id);
}
