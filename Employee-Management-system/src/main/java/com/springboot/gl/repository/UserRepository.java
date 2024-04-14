package com.springboot.gl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.springboot.gl.entity.User;





public interface UserRepository extends JpaRepository<User,Long>{
	
	Optional<User> getUserByUsername(String username);
	User findByUsername(String username);
}

