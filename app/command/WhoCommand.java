package command;

import msn.Messenger;
import net.sf.jml.MsnContact;
import net.sf.jml.MsnUserStatus;
import net.sf.jml.message.MsnInstantMessage;

public class WhoCommand implements Command {

  public String name() {
    return "!who";
  }

  public void process(Messenger messenger, MsnContact contact,
      String[] arguments) {
    MsnContact[] contacts = messenger.getContactList().getContacts();

    MsnInstantMessage response = new MsnInstantMessage();
    StringBuffer buffer = new StringBuffer();
    buffer.append("Online User(s)\n");
    int count = 0;
    for (MsnContact who : contacts) {
      if (!(who.getStatus().equals(MsnUserStatus.OFFLINE))) {
        if (count > 10) {
          response.setContent(buffer.toString());
          messenger.sendMessage(contact.getEmail(), response);

          response = new MsnInstantMessage();
          buffer = new StringBuffer();
          count = 0;
        }

        buffer.append(String.format("%s (%s) - %s\n", who.getFriendlyName(),
            who.getEmail(), who.getStatus()));
        count++;
      }
    }

    response.setContent(buffer.toString());
    messenger.sendMessage(contact.getEmail(), response);
  }

}
