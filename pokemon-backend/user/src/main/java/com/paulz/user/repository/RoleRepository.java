package com.paulz.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paulz.user.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    Role findByName(String roleName);
}
