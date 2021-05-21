package com.platzi.market.web.security.filter;

import com.platzi.market.domain.service.PlatziUserDetailsService;
import com.platzi.market.web.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilterRequest extends OncePerRequestFilter {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PlatziUserDetailsService platziUserDetailsService;

    // basically we are overriding this method i guess this is going to be executed
    // after every request we make, also this filter is passed to Security Config class
    // where we put the rule for every request we make
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // getting the request header authorization
        String authorizationHeader = request.getHeader("Authorization");

        // if has bearer as a first word
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            // getting the json web token
            String jwt = authorizationHeader.substring(7);
            // get the username with the method who unencrypt the token
            String username = jwtUtil.extractUsername(jwt);

            // if username is not null and there's not an authentication created
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // returning an user details with "platzi" as a password
                UserDetails userDetails = platziUserDetailsService.loadUserByUsername(username);

                // validate the jwt with de jwt and the userdetails previously created
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // this creates the authentication with spring clases
                    // the third parameters is for the roles
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
