package command;

import models.Group;
import msn.Messenger;
import net.sf.jml.MsnContact;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.JPAPlugin;

public class NickCommand implements Command {

  public String name() {
    return "!nick";
  }
  
  public void process(Messenger messenger, MsnContact contact,
      String[] arguments) {
    String nick = StringUtils.join(arguments, " ");

    JPAPlugin.startTx(false);

    String groupEmail = messenger.getOwner().getEmail().getEmailAddress();
    Group group = Group.find("byEmail", groupEmail).first();
    group.renameFriend(contact.getEmail().getEmailAddress(), nick);

    JPAPlugin.closeTx(false);

    messenger.sendText(contact.getEmail(), "Your name is changed");
  }

}
