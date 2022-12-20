package com.example.gamestore.repository;

import com.example.gamestore.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);
    User findUserByEmailAndPassword(String email, String password);
}
