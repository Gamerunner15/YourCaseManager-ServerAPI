package projects.Kolton.YourCaseManager_ServerAPI.DAOs;

import java.util.List;

import projects.Kolton.YourCaseManager_ServerAPI.exceptions.UserNotFoundException;
import projects.Kolton.YourCaseManager_ServerAPI.models.Address;
import projects.Kolton.YourCaseManager_ServerAPI.models.Login;
import projects.Kolton.YourCaseManager_ServerAPI.models.User;



public interface UserDAO {

	public boolean validateUsername(String username);
	public boolean validateLogin(String username, String password) throws UserNotFoundException;
	public User getUserInfo(String username) throws UserNotFoundException;
	public User getMedicalConditions(User user);
	public void addUser(Login login);
	public void updateUserAddress(User user);
	public void updateUserInfo(User user);
	public List<User> getListOfUsers();
}
