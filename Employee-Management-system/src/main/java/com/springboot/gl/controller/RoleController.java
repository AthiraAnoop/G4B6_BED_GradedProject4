package com.springboot.gl.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.gl.entity.Role;
import com.springboot.gl.repository.RoleRepository;

import com.springboot.gl.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
@RestController
public class RoleController {

	@Autowired
	private RoleRepository roleRepository;
	
	@PostMapping("/roles")
    public  ResponseEntity<ApiResponse<Role>> addRole(@RequestBody Role role) {
		
		Optional<Role> existingRole = roleRepository.findByName(role.getName());
	    if (existingRole.isPresent()) {
	        return ResponseEntity.ok(new ApiResponse<>("Already existed role", existingRole.get()));
	    }
	    String roleName = role.getName().toUpperCase();
	    role.setName(roleName);
	    
		Role savedRole = roleRepository.save(role);
		return ResponseEntity.ok(new ApiResponse<>("Successfully inserted", savedRole));
    }
}
