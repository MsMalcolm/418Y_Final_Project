package com.example.demo;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {

    @Autowired
    private UserRepository UserRepository;

    @Value("${accessKey}")
    String accessKey;
    @Value("${secretKey}")
    String secretKey;
    @Value("${bucketName}")
    String bucketName;

    //Id of the currently logged in user
    private Integer userID = -1;

    //Sets the default name on the homepage to Guest User
    //If a user logs in it displays that users name
    @GetMapping(path = "/")
    public ModelAndView homePage(Model view) {
        
        List<User> users = (List<User>) getAllUsers();

        //Sets default values to the guest account
        if(userID == -1){
            view.addAttribute("name", "Guest User");
            view.addAttribute("biography", "Introduce yourself by writing something in the bio section!");
            view.addAttribute("imgSrc", "https://smprofilepictures.s3.amazonaws.com/prof_pic.png");
        }else{
            String firstName = users.get(userID).getFirstName().toString();
            String lastName = users.get(userID).getLastName().toString();
            String biography; 
            String picture;

            //If the user doesn't have a picture/bio set default values
            try {
                biography = users.get(userID).getBiography().toString();
                picture = users.get(userID).getPicture().toString();
            
            } catch (Exception e) {
                biography = "Introduce yourself by writing something in the bio section!";
                picture = "https://smprofilepictures.s3.amazonaws.com/prof_pic.png";                   
                System.out.println("picture or biography not found");
            }

            view.addAttribute("name", firstName + " " + lastName);
            view.addAttribute("biography", biography);
            view.addAttribute("imgSrc", picture);
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

        //If no user is logged in, display default values on the edit page
        //Else display their info on the edit page
        if(userID == -1){
            view.addAttribute("firstname", "Guest");
            view.addAttribute("lastname", "User");
            view.addAttribute("biography", "Introduce yourself by writing something in the bio section!");
            view.addAttribute("imgSrc", "https://smprofilepictures.s3.amazonaws.com/prof_pic.png");
        }else{
            String firstName = users.get(userID).getFirstName().toString();
            String lastName = users.get(userID).getLastName().toString();
            String biography = users.get(userID).getBiography().toString();
            String picture = users.get(userID).getPicture().toString();

            view.addAttribute("biography", biography);
            view.addAttribute("firstname", firstName);
            view.addAttribute("lastname", lastName);
            view.addAttribute("imgSrc", picture);
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
        
        //Checks if the account exists before adding a new user.
        if(UserRepository.findByEmail(email) != null) {
            System.out.println("New user can't be made");
            view.addAttribute("accountError", "The email already exists. Try logging in");
            return new ModelAndView("register");
        }

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
    @RequestMapping(path = "/")
    public ModelAndView findUser(
        @RequestParam String email,
        @RequestParam String password,
        Model view
    ){
        //A list of all users in the database
        List<User> users = (List<User>) getAllUsers();

        Boolean emailFound = false;
        Boolean passwordFound = false;

        for(int i = 0; i < users.size(); i++){
        
            //Checks if the inputted email and password exists in the database
            if(email.equals(users.get(i).getEmail().toString())){
                emailFound = true;

                if(password.equals(users.get(i).getPassword().toString())){
                    passwordFound = true;

                    User user = UserRepository.findByEmail(email);
                    String firstName = users.get(i).getFirstName().toString();
                    String lastName = users.get(i).getLastName().toString();
                    String biography; 
                    String picture;

                    //If the user doesn't have a biography set default values and add it to the database
                    try {
                        biography = users.get(i).getBiography().toString();
                    } catch (Exception e) {
                        biography = "Introduce yourself by writing something in the bio section!";
                        user.setBiography(biography);
                        UserRepository.save(user);
                        System.out.println("biography not found");
                    }

                    //If the user doesn't have a picture set default values and add it to the database
                     try {
                        picture = users.get(i).getPicture().toString();
                    } catch (Exception e) {
                        picture = "https://smprofilepictures.s3.amazonaws.com/prof_pic.png";
                        user.setPicture(picture);
                        UserRepository.save(user);
                        System.out.println("biography not found");
                    }

                    //Stores the index where the user was found
                    userID = i;

                    //Returns user back to the home page with their information
                    view.addAttribute("biography", biography);
                    view.addAttribute("name", firstName + " " + lastName);
                    view.addAttribute("imgSrc", picture);
        
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

    // Links to the signout page: localhost:8080/signout
    @GetMapping(path = "/signout")
    public ModelAndView signoutUser(){
       
        // Sets the active user back to a guest account
        userID = -1;

        return new ModelAndView("signout");
    }

    //Connected to the edit form
    @RequestMapping(path = "/editVerification")
    public ModelAndView updateProfile(
        @RequestParam String name,
        @RequestParam String biography,
        Model view
    ){
        
        List<User> users = (List<User>) getAllUsers();
        
        if(userID != -1){
            String email = users.get(userID).getEmail().toString();

            User user = UserRepository.findByEmail(email);
            user.setBiography(biography);
            UserRepository.save(user);

            view.addAttribute("name", name);
            return new ModelAndView("editVerification");
        }

        return new ModelAndView("editVerification");
    }

    //Uploads an image to the AWS S3 Bucket, updates a users profile
    @PostMapping(value = "/upload")
    public ModelAndView uploads3(
        @RequestParam("photo") MultipartFile image, 
        @RequestParam(name = "desc") String desc,
        @RequestParam String firstname,
        @RequestParam String lastname,
        @RequestParam String biography)
    {
        ModelAndView returnPage = new ModelAndView();
        System.out.println("description: " + desc);
        System.out.println(image.getOriginalFilename());
    
        BasicAWSCredentials cred = new BasicAWSCredentials(accessKey, secretKey);
        
        AmazonS3 client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(cred))
                .withRegion(Regions.US_EAST_1).build();
        try {
            PutObjectRequest put = new PutObjectRequest(bucketName, image.getOriginalFilename(),
                    image.getInputStream(), new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead);
            client.putObject(put);

            //After an edit is submitted, the user should get sent to the verification page
            returnPage.setViewName("editVerification");
            
            List<User> users = (List<User>) getAllUsers();

            String imgSrc = "http://" + bucketName + ".s3.amazonaws.com/" + image.getOriginalFilename();
                
            //If the user doesn't upload a new image, then display their old one
            if(image.getOriginalFilename().equals("") && userID != -1){
                String photo = users.get(userID).getPicture().toString();
                imgSrc = photo;
            }

            //If a user is logged in, save the updated name, biography and image to the database
            if(userID != -1){
                String email = users.get(userID).getEmail().toString();
                User user = UserRepository.findByEmail(email);
                
                user.setBiography(biography);
                user.setPicture(imgSrc);
                user.setFirstName(firstname);
                user.setLastName(lastname);
                UserRepository.save(user);
                
                return returnPage;
            }else{
                returnPage.setViewName("error");
                returnPage.addObject("errorMsg", "Oops, looks like there was an error. Make sure you are signed in before you try to upload.");
            }
         
        } catch (IOException e) {
            e.printStackTrace();
            returnPage.setViewName("error");
        }
        return returnPage;
    }
}