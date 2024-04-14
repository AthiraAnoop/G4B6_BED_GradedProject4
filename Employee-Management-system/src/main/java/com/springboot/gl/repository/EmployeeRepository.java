package com.springboot.gl.repository;

import com.springboot.gl.entity.Employee;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
	Optional<Employee> findByFirstNameAndLastName(String firstName, String lastName);
	List<Employee> findByFirstName(String firstName);
}
