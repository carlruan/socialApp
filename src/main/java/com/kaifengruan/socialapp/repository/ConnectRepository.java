package com.kaifengruan.socialapp.repository;

import com.kaifengruan.socialapp.POJO.Connect;
import com.kaifengruan.socialapp.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectRepository extends JpaRepository<Connect, Long> {
    Connect findByConnectorIdAndUser(String connectorId, User user);
    List<Connect> findAllByUser(User user);

}
