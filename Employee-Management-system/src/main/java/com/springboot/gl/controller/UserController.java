package com.springboot.gl.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.gl.dto.ApiResponse;
import com.springboot.gl.entity.Role;
import com.springboot.gl.entity.User;
import com.springboot.gl.repository.RoleRepository;
import com.springboot.gl.repository.UserRepository;

import jakarta.persistence.EntityManager;

import com.springboot.gl.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private EntityManager entityManager;
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@PostMapping("/users")
	@Transactional
    public ResponseEntity<ApiResponse<User>> addUser(@RequestBody User user) {
		
		
	    if (user.getRoles() == null || user.getRoles().isEmpty()) {
	        return ResponseEntity.badRequest().body(new ApiResponse<>("Role validation failed - no roles provided", null));
	    }
	    Optional<User> existingUserOpt = userRepository.getUserByUsername(user.getUsername());
	    
	    if (existingUserOpt.isPresent()) {
			User existingUser = existingUserOpt.get();
			 if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
	                Set<Role> resolvedRoles = resolveRoles(user.getRoles());
	                
	                if (!doRolesMatch(existingUser.getRoles(), resolvedRoles)) {
	                    existingUser.getRoles().addAll(resolvedRoles);
	                    userRepository.save(existingUser);
	                    userRepository.flush();
	                    entityManager.refresh(existingUser);
	                    return ResponseEntity.ok(new ApiResponse<>("User updated with new roles", existingUser));
	                } else {
	                    return ResponseEntity.ok(new ApiResponse<>("User with the same roles already exists", existingUser));
	                }
	            } else {
	                return ResponseEntity.badRequest().body(new ApiResponse<>("Password mismatch", null));
	            }
	    }
	    
		Set<Role> resolvedRoles = new HashSet<>();
	    for (Role role : user.getRoles()) {
	        Optional<Role> existingRole = roleRepository.findById(role.getId());
	        if (!existingRole.isPresent() || !existingRole.get().getName().equals(role.getName())) {
	            
	            return ResponseEntity.badRequest().body(new ApiResponse<>("Role validation failed", null));
	        }
	        resolvedRoles.add(existingRole.get());
	    }

		user.setRoles(resolvedRoles);
	    user.setPassword(passwordEncoder.encode(user.getPassword()));
	    User savedUser = userRepository.save(user);
		return ResponseEntity.ok(new ApiResponse<>("Successfully inserted", savedUser));   
    }
	
	private Set<Role> resolveRoles(Set<Role> roles) {
        Set<Role> resolvedRoles = new HashSet<>();
        for (Role role : roles) {
            roleRepository.findById(role.getId()).ifPresent(resolvedRoles::add);
        }
        return resolvedRoles;
    }
	private boolean doRolesMatch(Set<Role> existingRoles, Set<Role> newRoles) {
		
		
	    // Check if every role in newRoles is present in existingRoles
	    for (Role newRole : newRoles) {
	        boolean matchFound = false;
	        for (Role existingRole : existingRoles) {
	            if (existingRole.getId().equals(newRole.getId()) && existingRole.getName().equals(newRole.getName())) {
	                matchFound = true;
	                break;
	            }
	        }
	        if (!matchFound) {
	            return false; // If any newRole is not found in existingRoles
	        }
	    }
	    
	    return true; // All roles matched
	}

}
