package com.api.service;


import com.api.client.UserDetailsServiceClient;
import com.api.model.UserDetails;
import com.api.repository.UserDetailsRepository;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceIntegration
{

    private final UserDetailsServiceClient userDetailsServiceClient;
    private final UserDetailsRepository userDetailsRepository;

    public UserDetailsServiceIntegration(UserDetailsServiceClient userDetailsServiceClient, UserDetailsRepository userDetailsRepository) {
        this.userDetailsServiceClient = userDetailsServiceClient;
        this.userDetailsRepository = userDetailsRepository;
    }

    public UserDetails createUserDetails(UserDetails userDetails) {
        return userDetailsRepository.save(userDetails);
    }

    public UserDetails fetchUserDetails(String userId) {
        return userDetailsServiceClient.loadUserByUserId(userId);
    }


}
