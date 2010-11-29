package notifiers;

import java.util.HashMap;

import models.User;
import play.Play;
import play.i18n.Messages;
import play.mvc.Mailer;
import play.mvc.Router;

public class Mails extends Mailer {

  public static void verify(User user) {
    setFrom(Play.configuration.getProperty("talks.from"));
    addRecipient(user.email);
    setSubject(Messages.get("mail.verify.subject"));

    HashMap<String, Object> arguments = new HashMap<String, Object>(2);
    arguments.put("id", user.id);
    arguments.put("code", user.code);

    String verify = Router.getFullUrl("Application.verify", arguments);
    send(verify);
  }

}
