package command;

import msn.Messenger;
import net.sf.jml.MsnContact;
import net.sf.jml.message.MsnInstantMessage;

public class ManCommand implements Command {

  public String name() {
    return "!man";
  }
  
  public void process(Messenger messenger, MsnContact contact,
      String[] arguments) {
    MsnInstantMessage response = new MsnInstantMessage();

    StringBuffer buffer = new StringBuffer();
    buffer.append("Commands\n");
    buffer.append("!nick <new name> : change name\n");
    buffer.append("!whoami : show current name\n");
    buffer.append("!who : list online users\n");
    buffer.append("!log <page> : show message log\n");
    response.setContent(buffer.toString());

    messenger.sendMessage(contact.getEmail(), response);
  }

}
