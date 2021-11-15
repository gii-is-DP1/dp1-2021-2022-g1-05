package org.springframework.samples.petclinic.web;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CurrentUserController {

    @GetMapping("/currentUser")
    public String showCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            if (authentication.isAuthenticated()){
                User currentUser = (User) authentication.getPrincipal();
                System.out.println(currentUser.getUsername());
            } else {
                System.out.println("User not authenticated");
            }

        }
        return "/welcome";
    }
}
