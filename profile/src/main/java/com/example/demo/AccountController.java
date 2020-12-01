package com.example.demo;

import java.util.List;
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

    //Id of the currently logged in user
    private Integer userID = -1;

    //Sets the default name on the homepage to Guest User
    //If a user logs in it displays that users name
    @GetMapping(path = "/")
    public ModelAndView homePage(Model view) {
        
        List<User> users = (List<User>) getAllUsers();

        if(userID == -1)
            view.addAttribute("name", "Guest User");
        else{
            String firstName = users.get(userID).getFirstName().toString();
            String lastName = users.get(userID).getLastName().toString();
            view.addAttribute("name", firstName + " " + lastName);
        }
        return new ModelAndView("home");
    }

    // Links to the login page: localhost:8080/login
    @GetMapping(path = "/login")
    public ModelAndView loginPage() {
        return new ModelAndView("login");
    }

    // Links to the register page: localhost:8080/register
    @GetMapping(path = "/register")
    public ModelAndView registrationPage() {
        return new ModelAndView("register");
    }

    // Links to the edit page: localhost:8080/edit
    @GetMapping(path = "/edit")
    public ModelAndView editPage(Model view) {
        List<User> users = (List<User>) getAllUsers();

        if(userID == -1)
            view.addAttribute("name", "Guest User");
        else{
            String firstName = users.get(userID).getFirstName().toString();
            String lastName = users.get(userID).getLastName().toString();
            view.addAttribute("name", firstName + " " + lastName);
        }

        return new ModelAndView("edit");
    }

    // Adds a new user to the SQL database
    // and takes them to their account page
    @RequestMapping(path = "/add")
    public ModelAndView addNewUser(
        @RequestParam String firstName, 
        @RequestParam String lastName,
        @RequestParam String email,
        @RequestParam String password, 
        Model view
    ){
        System.out.println(firstName + " " + lastName);

        User n = new User();
        n.setFirstName(firstName);
        n.setLastName(lastName);
        n.setEmail(email);
        n.setPassword(password);
        UserRepository.save(n);

        view.addAttribute("name", firstName + " " + lastName);
        return new ModelAndView("accountVerification");
    }

    // Returns a list of all users
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<User> getAllUsers() {

        return UserRepository.findAll();
    }

    // Searhes for a user by their id and returns that user
    @GetMapping(path = "/user")
    public @ResponseBody Optional<User> getOneUser(@RequestParam Integer id) {

        return UserRepository.findById(id);
    }

    //Connected to the login in form
    @RequestMapping(path = "/profile")
    public ModelAndView findUser(
        @RequestParam String email,
        @RequestParam String password,
        Model view
    ){
        List<User> users = (List<User>) getAllUsers();

        Boolean emailFound = false;
        Boolean passwordFound = false;

        //Checks if the inputted email and password exists in the database
        //Returns user back to the home page with their information
        for(int i = 0; i < users.size(); i++){
            
            if(email.equals(users.get(i).getEmail().toString())){
                emailFound = true;

                if(password.equals(users.get(i).getPassword().toString())){
                    passwordFound = true;

                    String firstName = users.get(i).getFirstName().toString();
                    String lastName = users.get(i).getLastName().toString();
                    
                    userID = i;

                    view.addAttribute("name", firstName + " " + lastName);

                    return new ModelAndView("home");
                }
            }        
        }
        
        //Returns user back to the login page with an error message  
        //if their information was not found.
        if((emailFound == false) && (passwordFound == false))
            view.addAttribute("accountError", "Account not found.");
        else if(emailFound == false)
            view.addAttribute("emailError", "Email was not found.");
        else if(passwordFound == false)
            view.addAttribute("passwordError", "Incorrect password.");
        
        return new ModelAndView("login");        
    }

    // Links to the login page: localhost:8080/login
    @GetMapping(path = "/signout")
    public ModelAndView signoutUser(){
        userID = -1;

        return new ModelAndView("signout");
    }
    
}