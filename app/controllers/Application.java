package controllers;

import models.User;
import play.Play;
import play.data.validation.Email;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;

public class Application extends Controller {

  public static void index() throws Throwable {
    if (Security.isConnected()) {
      Groups.index();
    } else {
      String property = Play.configuration.getProperty("talks.limit");
      Integer limit = Integer.parseInt(property);

      if (User.count() > limit) {
        Secure.login();
      } else {
        render();
      }
    }
  }

  public static void register(@Required String username,
      @Required String password, @Required String confirm,
      @Required @Email String email) throws Throwable {
    if (!Validation.hasErrors()) {
      if (confirm.equals(password)) {
        if (User.register(username, password, email)) {
          Groups.index();
        }
      }
    }
    index();
  }

  public static void verify(@Required Long id, @Required String code)
      throws Throwable {
    if (Validation.hasErrors()) {
      index();
    } else {
      User user = User.findById(id);
      user.verify(code);
      Secure.login();
    }
  }

}