package com.api.client;


import com.api.configuration.FeignConfiguration;
import com.api.model.UserDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "UserDetailsServiceClient" , url="${userdetailsapi-path}"
, configuration = FeignConfiguration.class)
public interface UserDetailsServiceClient {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    UserDetails loadUserByUserId(@RequestParam String userId);
}
