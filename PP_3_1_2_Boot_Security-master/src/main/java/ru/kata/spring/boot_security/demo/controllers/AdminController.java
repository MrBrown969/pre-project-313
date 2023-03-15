package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;


import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setbCryptPasswordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping()
    public String getAllUsers(Model model) {
        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("theUsers", allUsers);
        return "all-users";
    }

    @GetMapping("/new")
    public String addNewUser(Model model) {
        User newUser = new User();

        model.addAttribute("newUser", newUser);
        String adminRole = "ROLE_ADMIN";
        model.addAttribute("ROLE_ADMIN", adminRole);
        String userRole = "ROLE_USER";
        model.addAttribute("ROLE_USER", userRole);

        return "new-user";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("newUser") User user,
                           @RequestParam("name") String name,
                           @RequestParam("surname") String surname,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "admin", required = false) String adminRoleStr,
                           @RequestParam(value = "user", required = false) String userRoleStr
    ) {

        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        Set<Role> roles = new HashSet<>();
        if (adminRoleStr == null && userRoleStr == null) {
            throw new IllegalArgumentException("You have to choose at least one role");
        }
        if (adminRoleStr != null) {
            if (userService.getRoleByName(adminRoleStr) != null) {
                roles.add(userService.getRoleByName(adminRoleStr));
            } else {
                Role roleToAdd = new Role(adminRoleStr);
                roles.add(roleToAdd);
            }
        }
        if (userRoleStr != null) {
            if (userService.getRoleByName(userRoleStr) != null) {
                roles.add(userService.getRoleByName(userRoleStr));
            } else {
                Role roleToAdd = new Role(userRoleStr);
                roles.add(roleToAdd);
            }
        }
        user.setRoles(roles);

        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/updateUser")
    public String updateUser(Model model, @RequestParam("idToUpdate") Integer id) {
        model.addAttribute("userForUpdate", userService.getUserById(id));
        String adminRole = "ROLE_ADMIN";
        model.addAttribute("ROLE_ADMIN", adminRole);
        String userRole = "ROLE_USER";
        model.addAttribute("ROLE_USER", userRole);
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("userForUpdate") User user,
                         @RequestParam(value = "admin", required = false) String adminRoleStr,
                         @RequestParam(value = "user", required = false) String userRoleStr,
                         @RequestParam("idToUpdate") Integer id) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        if(userService.findByUsername(user.getUsername()) != null
                && userService.findByUsername(user.getUsername()).getId() != id) {
            throw new IllegalArgumentException("This username already exists");

        }
        if (adminRoleStr == null && userRoleStr == null) {
            throw new IllegalArgumentException("You have to choose at least one role");
        }
        if (adminRoleStr != null) {
            if (userService.getRoleByName(adminRoleStr) != null) {
                roles.add(userService.getRoleByName(adminRoleStr));
            } else {
                Role roleToAdd = new Role(adminRoleStr);
                roles.add(roleToAdd);
            }
        }
        if (userRoleStr != null) {
            if (userService.getRoleByName(userRoleStr) != null) {
                roles.add(userService.getRoleByName(userRoleStr));
            } else {
                Role roleToAdd = new Role(userRoleStr);
                roles.add(roleToAdd);
            }
        }
        user.setRoles(roles);

        userService.update(id, user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("idToDelete") Integer id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

}
