package projects.Kolton.YourCaseManager_ServerAPI.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import projects.Kolton.YourCaseManager_ServerAPI.DAOs.UserDAO;
import projects.Kolton.YourCaseManager_ServerAPI.exceptions.UserNotFoundException;
import projects.Kolton.YourCaseManager_ServerAPI.models.Login;
import projects.Kolton.YourCaseManager_ServerAPI.models.User;


/*
 * If you want to add preauthorization, you need to update the POM with:
 * <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
   </dependency>
 */


@RestController
public class UserController {
private UserDAO userDAO;
	
	public UserController(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@RequestMapping(path = "/users", method = RequestMethod.GET)
	public List<User> getListOfUsers(){
		return userDAO.getListOfUsers();
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(path = "/users", method = RequestMethod.POST)
	public void addUser(@RequestBody Login login) {
		userDAO.addUser(login);
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(path = "/users", method = RequestMethod.PUT)
	public void updateUser(@RequestBody User user) {
		userDAO.updateUserInfo(user);
	}
	
	@RequestMapping(path = "/users/{username}", method = RequestMethod.GET)
	public User getUserInfo(@PathVariable String username) throws UserNotFoundException {
		return userDAO.getUserInfo(username);
	}
	
	@RequestMapping(path = "/validate", method = RequestMethod.POST)
	public boolean validateLogin(@RequestBody Login login) throws UserNotFoundException {
		String username = login.getUsername();
		String password = login.getPassword();
		return userDAO.validateLogin(username, password);
	}
	
	@RequestMapping(path = "/validate/{username}", method = RequestMethod.GET)
	public boolean validateUsername(@PathVariable String username) {
		return userDAO.validateUsername(username);
	}
	

}
