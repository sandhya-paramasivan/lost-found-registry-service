package com.api.controller;

import com.api.model.User;
import com.api.service.LostItemsRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class CreateUserRoleController {
    private final LostItemsRegistryService lostItemsRegistryService;

    public CreateUserRoleController(LostItemsRegistryService lostItemsRegistryService) {
        this.lostItemsRegistryService = lostItemsRegistryService;
    }

    @PostMapping("/createUser")
    public String createUserRoles(@RequestBody User user){
        return lostItemsRegistryService.createRoles(user);
    }

}
