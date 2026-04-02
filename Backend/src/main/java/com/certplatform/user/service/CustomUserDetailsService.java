package com.certplatform.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.certplatform.admin.entity.Admin;
import com.certplatform.admin.repository.AdminRepository;
import com.certplatform.common.entity.BaseUser;
import com.certplatform.common.enums.Role;
import com.certplatform.user.entity.Student;
import com.certplatform.user.repository.StudentRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private AdminRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Student student = studentRepo.findByEmail(email).orElse(null);
        if (student != null) {
            return toUserDetails(student, Role.STUDENT);
        }

        Admin admin = adminRepo.findByEmail(email).orElse(null);
        if (admin != null) {
            return toUserDetails(admin, Role.ADMIN);
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    private UserDetails toUserDetails(BaseUser user, Role role) {
        String authority = role.name();
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            List.of(new SimpleGrantedAuthority(authority))
        );
    }
}

