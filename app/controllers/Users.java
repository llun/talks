package controllers;

import models.User;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Users extends Controller {

  public static void profile() {
    User user = User.fromUsername(Security.connected());
    render(user);
  }

  public static void changePassword(@Required String password,
      @Required String confirm) {
    if (Validation.hasErrors()) {
      Validation.keep();
      params.flash();
    } else {
      if (password.equals(confirm)) {
        User user = User.fromUsername(Security.connected());
        user.hash = Codec.hexSHA1(password);
        user.save();
      }
    }
    profile();
  }

}
