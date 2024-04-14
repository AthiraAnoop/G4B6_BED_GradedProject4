package com.springboot.gl.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import com.springboot.gl.entity.Role;
import com.springboot.gl.entity.User;

public class UserInfoUserDetails implements UserDetails{

	
	private String username;
	private String password;
	
	private List<GrantedAuthority> authorities;
	
	
	
	public UserInfoUserDetails(User userinfo) {
		username=userinfo.getUsername();
		password=userinfo.getPassword();
		this.authorities = userinfo.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
				
	}
   
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
         
        return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
