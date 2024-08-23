package com.abc.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.abc.entity.UserDetailsImpl;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;


@Component
public class TestFilter implements Filter {
	
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("Executing Test filter: LoggingFilter");
 	   Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	   UserDetailsImpl userDetails = null;
	if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
		userDetails = (UserDetailsImpl) authentication.getPrincipal();
		System.out.println("userDetails is from context "+userDetails);
	}
        chain.doFilter(request, response);
    }

}
