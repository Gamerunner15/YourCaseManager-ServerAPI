package projects.Kolton.YourCaseManager_ServerAPI.DAOs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import projects.Kolton.YourCaseManager_ServerAPI.exceptions.UserNotFoundException;
import projects.Kolton.YourCaseManager_ServerAPI.models.Address;
import projects.Kolton.YourCaseManager_ServerAPI.models.Login;
import projects.Kolton.YourCaseManager_ServerAPI.models.MedicalCondition;
import projects.Kolton.YourCaseManager_ServerAPI.models.User;

@Component // Tells Spring this can be injected somewhere
public class JDBCUserDAO implements UserDAO {

	private JdbcTemplate template;

	public JDBCUserDAO(JdbcTemplate template) {
		this.template = template;
	}

	public boolean validateUsername(String username) {
		String sql = "SELECT username FROM app_user WHERE username = ?;";
		SqlRowSet row = template.queryForRowSet(sql, username);
		if (row.next()) {
			return false;
		} else {
			return true;
		}
	}

	public boolean validateLogin(String username, String password) throws UserNotFoundException {
		String sql = "SELECT password FROM app_user WHERE username = ?";
		SqlRowSet row = template.queryForRowSet(sql, username);
		if (row.next()) {
			String actualPassword = row.getString("password");
			if (actualPassword.equals(password)) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new UserNotFoundException();
		}
	}

	public User getUserInfo(String username) throws UserNotFoundException {
		String sql = "SELECT * FROM app_user " + "WHERE app_user.username = ?;";
		SqlRowSet row = template.queryForRowSet(sql, username);

		if (row.next()) {
			User newUser = new User();
			newUser.setId(row.getInt("id"));
			newUser.setUsername(row.getString("username"));
			newUser.setEmailAddress(row.getString("email_address"));
			newUser.setFirstName(row.getString("first_name"));
			newUser.setLastName(row.getString("last_name"));
			newUser.setAddressId(row.getInt("address_id"));
			newUser.setBirthDate(row.getString("birth_date"));
			newUser.setAnnualIncome(row.getDouble("annual_income"));
			newUser.setGender(row.getString("gender"));
			newUser.setNeedsCellPhone(row.getBoolean("needs_phone"));
			newUser.setNeedsFood(row.getBoolean("needs_food"));
			newUser.setNeedsJob(row.getBoolean("needs_job"));
			newUser.setNeedsHousing(row.getBoolean("needs_housing"));

			if (newUser.getAddressId() > 0) {
				String sql2 = "SELECT * FROM address WHERE id = ?";
				SqlRowSet row2 = template.queryForRowSet(sql2, newUser.getAddressId());
				row2.next();
				Address address = new Address(row2.getString("street_address"), row2.getString("city"),
						row2.getString("State"), row2.getInt("zipcode"), row2.getString("building_type"));
				newUser.setAddress(address);
			}

			return this.getMedicalConditions(newUser);
		} else {
			throw new UserNotFoundException();
		}

	}

	public User getMedicalConditions(User user) {
		String sql = "SELECT * FROM medical_condition "
				+ "JOIN app_user_medical_condition ON app_user_medical_condition.condition_id = medical_condition.id "
				+ "WHERE app_user_medical_condition.user_id = ?;";
		SqlRowSet row = template.queryForRowSet(sql, user.getId());
		List<MedicalCondition> conditionList = new ArrayList<MedicalCondition>();
		while (row.next()) {
			MedicalCondition condition = new MedicalCondition();
			condition.setName(row.getString("name"));
			condition.setType(row.getString("type"));
			condition.setDuration(row.getInt("duration"));
			condition.setDescription(row.getString("description"));
			conditionList.add(condition);
		}
		user.setMedicalConditions(conditionList);
		return user;
	}

	public void addUser(Login login) {
		String sql = "INSERT INTO app_user (username, password, email_address) VALUES (?, ?, ?)";
		template.update(sql, login.getUsername(), login.getPassword(), login.getEmailAddress());
	}

	public void updateUserAddress(User user) {
		Address address = user.getAddress();
		String sql1 = "INSERT INTO address (street_address, city, state, zipcode, building_type) VALUES (?, ?, ?, ?, ?) Returning id;";
		SqlRowSet addressId = template.queryForRowSet(sql1, address.getStreetAddress(), address.getCityName(),
				address.getStateName(), address.getZipCode(), address.getBuildingType());
		addressId.next();
		int id = addressId.getInt("id");
		String sql2 = "UPDATE app_user SET address_id = ? WHERE id = ?;";
		template.update(sql2, id, user.getId());

	}

	public void updateUserInfo(User user) {
		String sql = "UPDATE app_user "
				+ "SET birth_date = CAST(? AS DATE), first_name = ?, last_name = ?, email_address = ?, "
				+ "gender = ?, " + "annual_income = ?, " + "needs_phone = ?, " + "needs_job = ?, " + "needs_food = ?, "
				+ "needs_housing = ?, needs_transport = ? " + "WHERE id = ?";
		template.update(sql, user.getBirthDate(), user.getFirstName(), user.getLastName(), user.getEmailAddress(),
				user.getGender(), user.getAnnualIncome(), user.isNeedsCellPhone(), user.isNeedsJob(),
				user.isNeedsFood(), user.isNeedsHousing(), user.isNeedsTransport(), user.getId());
		this.updateMedicalConditions(user);
		// Check for Address update
		if (user.getAddressId() > 0) {
			String sql2 = "SELECT * FROM address WHERE id = ?";
			SqlRowSet row = template.queryForRowSet(sql2, user.getAddressId());
			row.next();
			if (!(row.getString("street_address").equals(user.getAddress().getStreetAddress()))) {
				//Actually do the update
				this.updateUserAddress(user);
			}
		}
	}

	// TODO
	public void updateMedicalConditions(User user) {
		// String sql = "UPDATE medical_condition SET "
	}

	public List<User> getListOfUsers() {
		String sql = "SELECT * FROM app_user;";
		SqlRowSet rowset = template.queryForRowSet(sql);
		List<User> users = this.mapRowToUser(rowset);

		return users;
	}

	public List<User> mapRowToUser(SqlRowSet rowset) {
		List<User> users = new ArrayList<User>();
		while (rowset.next()) {
			User user = new User();
			user.setUsername(rowset.getString("username"));
			user.setFirstName(rowset.getString("first_name"));
			user.setLastName(rowset.getString("last_name"));
			user.setAnnualIncome(rowset.getDouble("annual_income"));
			user.setGender(rowset.getString("gender"));
			user.setNeedsCellPhone(rowset.getBoolean("needs_phone"));
			user.setNeedsFood(rowset.getBoolean("needs_food"));
			user.setNeedsJob(rowset.getBoolean("needs_job"));
			user.setNeedsHousing(rowset.getBoolean("needs_housing"));
			users.add(user);
		}

		return users;
	}

}
