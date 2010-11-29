package msn;

import models.Group;
import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnOwner;
import net.sf.jml.event.MsnMessengerAdapter;
import net.sf.jml.event.MsnMessengerListener;
import play.Logger;
import play.db.jpa.JPAPlugin;

public class MessengerListener implements MsnMessengerListener {

  public static final MessengerListener instance = new MessengerListener();

  public MessengerListener() {
    super();
  }

  public void exceptionCaught(MsnMessenger messenger, Throwable throwable) {
    Logger.error(throwable, "Something wrong");
  }

  public void loginCompleted(MsnMessenger messenger) {
    MsnOwner owner = messenger.getOwner();
    JPAPlugin.startTx(false);
    Group group = Group.find("byEmail", owner.getEmail().getEmailAddress())
        .first();
    group.online = true;
    group.save();
    JPAPlugin.closeTx(false);
    
    owner.setDisplayName(group.display);

    Logger.info("%s Login complete", owner.getEmail());
  }

  public void logout(MsnMessenger messenger) {
    Logger.info("Logout complete");
  }

}
