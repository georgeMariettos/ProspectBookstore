package com.unipi.prospect;

import org.springframework.boot.SpringApplication;
import com.unipi.prospect.db.users.UserDAO;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProspectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProspectApplication.class, args);
		//UserDAO userDAO = new UserDAO();
		//userDAO.updateAllUsersPasswords();

	}
	//testing
	//testing ant

}




