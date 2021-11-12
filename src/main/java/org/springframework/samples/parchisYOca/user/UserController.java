package org.springframework.samples.parchisYOca.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.achievement.Achievement;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping()
    public String listadoUsers(ModelMap modelMap){
        String vista = "users/listUsers";
        Iterable<User> users = userService.findAll();
        modelMap.addAttribute("users",users);
        return vista;
    }

    @GetMapping(path="/new")
    public String crearUser(ModelMap modelMap){
        String view = "users/editUser";
        modelMap.addAttribute("user", new User());
        return view;
    }

    @PostMapping(path="/save")
    public String salvarUser(@Valid User user, BindingResult result, ModelMap modelMap){
        String view="users/listUsers";
        if(result.hasErrors()){
            modelMap.addAttribute("user",user);
            return "users/editUser";
        }else{
            userService.save(user);
            modelMap.addAttribute("message", "User successfully saved!");
            view=listadoUsers(modelMap);
        }
        return view;
    }

    @GetMapping(path="/delete/{userId}")
    public String borrarUser(@PathVariable("userId") int userId, ModelMap modelMap){
        String view = "users/listUsers";
        Optional<User> user = userService.findUserById(userId);
        if(user.isPresent()){
            userService.delete(user.get());
            modelMap.addAttribute("message", "User successfully deleted!");
            view=listadoUsers(modelMap);

        }else{
            modelMap.addAttribute("message", "User not found!");
            view=listadoUsers(modelMap);
        }
        return view;
    }
}
