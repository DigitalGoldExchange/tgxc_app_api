package com.dgex.backend.security;

import com.dgex.backend.exception.LoginException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Override

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/"));
        super.successfulAuthentication(request, response, chain, authResult);
    }


    @Override

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException exception) throws IOException, ServletException {

        Integer errorType = 1;
        String message = exception.getMessage();

        if(exception instanceof LoginException) {
            errorType = ((LoginException) exception.getCause()).getErrorType();
        }else{
            if(exception instanceof BadCredentialsException) message = "로그인 정보가 올바르지 않습니다. 다시 시도하세요";
        }

        request.setAttribute("error", exception.getMessage());
        response.sendRedirect(
                String.format("/login?error=%d&username=%s&message=%s", errorType, URLEncoder.encode(request.getParameter("username"),"UTF-8"),
                        URLEncoder.encode(message,"UTF-8")));
        // System.out.println("unsuccessfulAuthentication myField = ");
        // setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error=true"));
        // super.unsuccessfulAuthentication(request, response, failed);
    }
}
