package command;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import models.Group;
import msn.Messenger;
import net.sf.jml.MsnContact;
import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnUserStatus;
import net.sf.jml.message.MsnInstantMessage;
import play.Logger;
import play.Play;
import play.db.jpa.JPAPlugin;

public class Processor {

  public static final Processor instance = new Processor();
  private final HashMap<String, Command> commands;

  public Processor() {
    commands = new HashMap<String, Command>();

    String commandClasses = Play.configuration.getProperty("talks.commands", "");
    String classes[] = StringUtils.split(commandClasses, ",");
    for (String className : classes) {
      try {
        Class<?> clazz = Play.classloader.loadClass(String.format("command.%s",
            className));
        Command command = (Command) clazz.newInstance();
        String name = command.name();

        commands.put(name, command);
      } catch (Exception e) {
        Logger.error("Can't load command %s", className);
      }
    }
  }

  public boolean process(Messenger messenger, MsnContact contact, String message) {
    boolean result = false;

    String[] messages = StringUtils.split(message);
    Command command = commands.get(messages[0]);
    if (command != null) {
      String[] arguments = (String[]) ArrayUtils.subarray(messages, 1,
          message.length());
      command.process(messenger, contact, arguments);
      result = true;
    }

    return result;
  }

}
