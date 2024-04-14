package com.springboot.gl.controller;

import com.springboot.gl.dto.ApiResponse;
import com.springboot.gl.entity.Employee;
import com.springboot.gl.entity.Role;
import com.springboot.gl.repository.EmployeeRepository;
import com.springboot.gl.service.EmployeeService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.gl.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    //Create Employees
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Employee>> addEmployee(@RequestBody Employee employee) {
    	Optional<Employee> existingEmployee = employeeRepository.findByFirstNameAndLastName(employee.getFirstName(), employee.getLastName());
    	if (existingEmployee.isPresent()) {
	        return ResponseEntity.ok(new ApiResponse<>("Already existed role", existingEmployee.get()));
	    }
    	Employee savedEmployee = employeeService.addEmployee(employee);
    	return ResponseEntity.ok(new ApiResponse<>("Successfully inserted", savedEmployee));
    }
    
    //Fetch Employees
    @GetMapping("/getEmployees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return ResponseEntity.ok(employees);
    }
    
    //Fetch Employee By Id
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    //Update Employee Data By Id
    @PostMapping("/update")
    public ResponseEntity<?> updateEmployee(@RequestBody Employee employeeDetails) {
    	Long id = employeeDetails.getId();
    	Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null) {
            return ResponseEntity.ok(new ApiResponse<>("Employee not found with id " + id, null));
        }
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());

        Employee updatedEmployee = employeeRepository.save(employee);
        return ResponseEntity.ok(updatedEmployee);
    }
    
    //Delete Employee By Id
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> deleteEmployee(@PathVariable Long id) {
    	Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            employeeRepository.delete(employee.get());  // Deleting the found employee
            return ResponseEntity.ok(new ApiResponse<>("Successfully Deleted  Employee Id " + id, null));  // Return HTTP 200 OK to indicate successful deletion
        } else {
        	return ResponseEntity.ok(new ApiResponse<>("Employee not found with id " + id, null));
        }
    }
    
    //Search Employee By First Name
    @GetMapping("/search/{firstName}")
    public ResponseEntity<ApiResponse<List<Employee>>> getEmployeesByFirstName(@PathVariable String firstName) {
        List<Employee> employees = employeeRepository.findByFirstName(firstName);
        if (employees.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>("No employees found with the given first name.", employees));
        }
        return ResponseEntity.ok(new ApiResponse<>("Employees found.", employees));
    }
    
    //Sort Employees ASC or DESC  Based on FirstName
    @GetMapping("/sort")
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployeesSorted(@RequestParam String order) {
    	Sort.Direction sortDirection = "asc".equalsIgnoreCase(order.replace("\"", "").trim()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Employee> employees = employeeRepository.findAll(Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(new ApiResponse<>("Sorted employee list.", employees));
    }
}
