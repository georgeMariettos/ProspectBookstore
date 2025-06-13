package com.unipi.prospect;

import com.unipi.prospect.users.Admin;
import org.springframework.boot.SpringApplication;
import com.unipi.prospect.db.users.UserDAO;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProspectApplication {

	public static void main(String[] args) {
		//initialize default admin if there's no admins in database
		if(new UserDAO().selectAll("Admin").isEmpty()){
			new UserDAO().insert(new Admin("admin", "123", "default", "admin", true));
		}
		SpringApplication.run(ProspectApplication.class, args);
		//UserDAO userDAO = new UserDAO();
		//userDAO.updateAllUsersPasswords();

	}
	//testing
	//testing ant

}




