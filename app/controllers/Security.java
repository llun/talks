package controllers;

import play.Logger;
import models.User;

public class Security extends controllers.Secure.Security {

  static boolean authenticate(String username, String password) {
    Boolean result = User.authenticate(username, password);
    Logger.info("Username: %s login %s", username, result ? "success" : "fail");
    return result;
  }

}
