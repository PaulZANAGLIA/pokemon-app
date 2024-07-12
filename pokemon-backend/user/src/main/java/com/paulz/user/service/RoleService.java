package com.paulz.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paulz.user.entity.Role;
import com.paulz.user.repository.RoleRepository;



@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role createRole(String roleName){
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }

    public List<Role> getRoles(){
        return this.roleRepository.findAll();
    }

    public Role getRoleByName(String roleName){
        return this.roleRepository.findByName(roleName);
    }
}
