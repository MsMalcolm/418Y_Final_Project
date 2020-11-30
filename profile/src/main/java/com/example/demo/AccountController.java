package com.example.demo;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {

    @Autowired
    private UserRepository UserRepository;

    // Greets the user with a message "Hello, name"
    // @GetMapping("/")
    // public String greet(@RequestParam(name="name", required = false, defaultValue = "Sign in")
    //     String name, Model view
    // ){
    //     String s = new String("Hello, " + name);
    //     view.addAttribute("renderText", s);
    //     return "greetingView";
    // }

    //Gets info from login form and displays the users name
    // @PostMapping("/profile")
    // public String saveUserInfo(
    //     @RequestParam(name = "fname", required = true) String fname,
    //     @RequestParam(name = "lname", required = true) String lname,
    //     Model view
    // ){
    //     System.out.println(fname + " "+ lname);
    //     view.addAttribute("name", fname + " " + lname);
        
    //     return "accountPage";
    // }
    @GetMapping(path="/")
    public ModelAndView homePage(
        // @RequestParam String firstName,
        // @RequestParam String lastName,
        // Model view
    ){
        // view.addAttribute("name", firstName + " " + lastName);
        
        return new ModelAndView("home");
    }

    @GetMapping(path="/login")
    public ModelAndView loginPage(){
        return new ModelAndView("login");
    }

    @GetMapping(path="/register")
    public ModelAndView registrationPage(){
        return new ModelAndView("register");
    }

    @GetMapping(path="/edit")
    public ModelAndView editPage(){
        return new ModelAndView("edit");
    }

    //Adds a new user to the SQL database  
    //and takes them to their account page
    @RequestMapping(path="/add")
    public ModelAndView addNewUser (
        @RequestParam String firstName,
        @RequestParam String lastName,
        @RequestParam String email,
        @RequestParam String password,
        Model view
    ){
        System.out.println(firstName + " "+ lastName);
        
        User n = new User();
        n.setFirstName(firstName);
        n.setLastName(lastName);
        n.setEmail(email);
        n.setPassword(password);
        UserRepository.save(n);

        view.addAttribute("name", firstName + " " + lastName);
        return new ModelAndView("accountVerification");   
    }

    //Returns a list of all users
    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers(){

        return UserRepository.findAll();
    }

    //Searhes for a user by their id and returns that user
    @GetMapping(path="/user")
    public @ResponseBody Optional<User> getOneUser(@RequestParam Integer id){
     
        return UserRepository.findById(id);
    }

    // @GetMapping(path="/userName")
    // public @ResponseBody Optional<User> getUsername(@RequestParam String firstName){
     
    //     return UserRepository.findByName(firstName);
    // }



}