package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.ModelAndView;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JUnitTest {
    
    @MockBean
    private UserRepository repository;

    @Test
    public void saveUserTest(){
    
        User user = new User();
        user.setFirstName("Ebony");
        user.setLastName("Myers");
        user.setEmail("ebonymyers@email.com");
        user.setPassword("biggie");
        when(repository.save(user)).thenReturn(user);
        assertEquals("Ebony", user.getFirstName());
        assertEquals("Myers", user.getLastName());
        assertEquals("ebonymyers@email.com", user.getEmail());
        assertEquals("biggie", user.getPassword());
    }

    @Test
    public void returnToTheRightView() {
        ModelAndView view = new ModelAndView();
        view.setViewName("login");
        
        boolean emailFound = false;
        boolean passwordFound = false;
        
        // Returns user back to the login page with an error message
        //if their information was not found.
        if((emailFound == false) && (passwordFound == false))
            view.addObject("accountError", "Account not found.");
        else if(emailFound == false)
            view.addObject("emailError", "Email was not found.");
        else if(passwordFound == false)
            view.addObject("passwordError", "Incorrect password.");
        else
            view.setViewName("home");

        String viewName  = view.getViewName();
        assertEquals(viewName, "login");        
    }
}
