package msn;

import java.util.Date;

import models.Group;
import models.MessageLog;
import net.sf.jml.MsnContact;
import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnOwner;
import net.sf.jml.MsnProtocol;
import net.sf.jml.MsnSwitchboard;
import net.sf.jml.event.MsnMessageListener;
import net.sf.jml.message.MsnControlMessage;
import net.sf.jml.message.MsnDatacastMessage;
import net.sf.jml.message.MsnInstantMessage;
import net.sf.jml.message.MsnSystemMessage;
import net.sf.jml.message.MsnUnknownMessage;
import net.sf.jml.message.p2p.MsnFileDeclineMessage;
import net.sf.jml.message.p2p.MsnFileInviteMessage;
import net.sf.jml.message.p2p.MsnFileOkMessage;
import net.sf.jml.message.p2p.MsnP2PDataMessage;
import net.sf.jml.message.p2p.MsnP2PMessage;
import net.sf.jml.protocol.MsnIncomingMessage;
import net.sf.jml.protocol.MsnOutgoingMessage;
import net.sf.jml.protocol.outgoing.OutgoingMSG;
import play.Logger;
import play.db.jpa.JPAPlugin;

import command.Processor;

public class MessageListener implements MsnMessageListener {

  public static final MessageListener instance = new MessageListener();

  public void controlMessageReceived(MsnSwitchboard switchboard,
      MsnControlMessage message, MsnContact contact) {
  }

  public void datacastMessageReceived(MsnSwitchboard switchboard,
      MsnDatacastMessage message, MsnContact contact) {
    Messenger messenger = (Messenger) switchboard.getMessenger();
    MsnContact[] contacts = messenger.getContactList().getContacts();
    for (MsnContact other : contacts) {
      if (!other.getEmail().getEmailAddress()
          .equals(contact.getEmail().getEmailAddress())) {
        messenger.sendMessage(other.getEmail(), message);
      }
    }
  }

  public void instantMessageReceived(MsnSwitchboard switchboard,
      MsnInstantMessage message, MsnContact contact) {
    Messenger messenger = (Messenger) switchboard.getMessenger();

    Processor processor = Processor.instance;
    if (!processor.process(messenger, contact, message.getContent())) {
      String announce = String.format("%s: %s", contact.getFriendlyName(),
          message.getContent());
      JPAPlugin.startTx(false);

      Group group = Group.fromEmail(messenger.getOwner().getEmail()
          .getEmailAddress());
      MessageLog.log(group, contact.getEmail().getEmailAddress(), announce);

      JPAPlugin.closeTx(false);

      message.setContent(announce);
      MsnContact[] contacts = messenger.getContactList().getContacts();
      for (MsnContact receiver : contacts) {
        if (!receiver.getEmail().getEmailAddress()
            .equals(contact.getEmail().getEmailAddress())) {
          messenger.sendMessage(receiver.getEmail(), message);
        }
      }
    }
  }

  public void p2pMessageReceived(MsnSwitchboard switchboard,
      MsnP2PMessage message, MsnContact contact) {
    MsnFileDeclineMessage declineMessage = new MsnFileDeclineMessage();
    Messenger messenger = (Messenger) switchboard.getMessenger();
    messenger.sendMessage(contact.getEmail(), declineMessage);
  }

  public void systemMessageReceived(MsnMessenger messenger,
      MsnSystemMessage message) {
  }

  public void unknownMessageReceived(MsnSwitchboard switchboard,
      MsnUnknownMessage message, MsnContact contact) {
  }

  public void offlineMessageReceived(String body, String contentType,
      String encoding, Date date, MsnContact contact) {

  }

}
