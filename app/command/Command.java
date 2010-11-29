package command;

import msn.Messenger;
import net.sf.jml.MsnContact;

public interface Command {

  String name();
  void process(Messenger messenger, MsnContact contact, String[] arguments);

}
