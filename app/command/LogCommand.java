package command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.Group;
import models.MessageLog;
import msn.Messenger;
import net.sf.jml.MsnContact;
import net.sf.jml.message.MsnInstantMessage;
import play.db.jpa.JPAPlugin;
import play.templates.JavaExtensions;

public class LogCommand implements Command {

  public String name() {
    return "!log";
  }

  public void process(Messenger messenger, MsnContact contact,
      String[] arguments) {
    String email = messenger.getOwner().getEmail().getEmailAddress();
    JPAPlugin.startTx(true);
    Group group = Group.find("byEmail", email).first();
    Map<String, List<MessageLog>> logs;
    if (arguments.length == 1) {
      try {
        Integer page = Integer.parseInt(arguments[0]);
        logs = group.log(page, false);
      } catch (NumberFormatException e) {
        // Return first page.
        logs = group.log(1, false);
      }
    } else {
      logs = group.log(1, false);
    }

    StringBuffer buffer = new StringBuffer();
    for (String key : logs.keySet()) {
      buffer.append(key.concat("\n"));
      
      List<MessageLog> groupLog = logs.get(key);
      for (MessageLog log : groupLog) {
        buffer.append(log.message.concat("\n"));
      }
      buffer.append("\n");
    }
    
    MsnInstantMessage response = new MsnInstantMessage();
    response.setContent(buffer.toString());
    messenger.sendMessage(contact.getEmail(), response);

    JPAPlugin.closeTx(false);
  }

}
