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
   /*     Role adminForSing = new Role("ROLE_ADMIN");
        Role userForSing = new Role("ROLE_USER");
        Set<Role> roleSet = new HashSet<>();
        roleSet = Collections.singleton(adminForSing);*/
        model.addAttribute("newUser", newUser);
       String adminRole = "ROLE_ADMIN";
        model.addAttribute("ROLE_ADMIN",adminRole);
        String userRole = "ROLE_USER";
        model.addAttribute("ROLE_USER",userRole);

        return "new-user";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("newUser") User user,
                           @RequestParam("name") String name,
                           @RequestParam("surname") String surname,
                          @RequestParam("rolesFromFront") String role,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password
                           ) {
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        Role role1 = new Role(role);//todo think about singleton
        Set<Role> roles = new HashSet<>();
        roles.add(role1);
       // user.setRoles(Collections.singleton(role));
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/updateUser")
    public String updateUser(Model model, @RequestParam("idToUpdate") Integer id) {
        model.addAttribute("userForUpdate", userService.getUserById(id));

        //todo here I am adding new stuff
        List<Role> listOfRoles = userService.getRoles();
        model.addAttribute("listOfRoles",listOfRoles);

        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("userForUpdate") User user
            , @RequestParam("idToUpdate") Integer id) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userService.update(id, user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("idToDelete") Integer id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

}
