package com.paulz.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paulz.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    // Below methods are not handle by the parent class because they are more specifics
    List<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
