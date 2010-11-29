package command;

import msn.Messenger;
import net.sf.jml.MsnContact;

public class WhoamICommand implements Command {

  public String name() {
    return "!whoami";
  }
  
  public void process(Messenger messenger, MsnContact contact, String[] arguments) {
    messenger.sendText(contact.getEmail(),
        String.format("Name: %s", contact.getFriendlyName()));
  }

}
