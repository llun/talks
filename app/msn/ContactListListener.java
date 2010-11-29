package msn;

import java.util.List;

import models.Alias;
import models.Group;
import net.sf.jml.Email;
import net.sf.jml.MsnContact;
import net.sf.jml.MsnContactPending;
import net.sf.jml.MsnGroup;
import net.sf.jml.MsnList;
import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnOwner;
import net.sf.jml.MsnUserStatus;
import net.sf.jml.event.MsnContactListListener;
import play.Logger;
import play.db.jpa.JPAPlugin;

public class ContactListListener implements MsnContactListListener {

  public static final ContactListListener instance = new ContactListListener();

  public void contactAddCompleted(MsnMessenger messenger, MsnContact contact,
      MsnList list) {
    Email ownerEmail = messenger.getOwner().getEmail();
    Logger.info("(%s) User: %s is added", ownerEmail, contact.getEmail());
  }

  public void contactAddInGroupCompleted(MsnMessenger messenger,
      MsnContact contact, MsnGroup group) {
  }

  public void contactAddedMe(MsnMessenger messenger, MsnContact contact) {
    Email ownerEmail = messenger.getOwner().getEmail();
    Logger.info("(%s) User: %s adds me from their list", ownerEmail,
        contact.getEmail());
    messenger.addFriend(contact.getEmail(), contact.getDisplayName());
    messenger.unblockFriend(contact.getEmail());

    JPAPlugin.startTx(false);

    String groupEmail = messenger.getOwner().getEmail().getEmailAddress();
    String contactEmail = contact.getEmail().getEmailAddress();

    Group group = Group.find("byEmail", groupEmail).first();
    Alias alias = new Alias(group, contactEmail, contact.getFriendlyName());
    alias.save();

    JPAPlugin.closeTx(false);
  }

  public void contactAddedMe(MsnMessenger messenger,
      MsnContactPending[] pendings) {
    Email ownerEmail = messenger.getOwner().getEmail();
    for (MsnContactPending pending : pendings) {
      Logger.info("(%s) User: %s adds me from their list", ownerEmail,
          pending.getEmail());
      messenger.addFriend(pending.getEmail(), pending.getDisplayName());

      JPAPlugin.startTx(false);

      String groupEmail = messenger.getOwner().getEmail().getEmailAddress();
      String contactEmail = pending.getEmail().getEmailAddress();

      Group group = Group.find("byEmail", groupEmail).first();
      Alias alias = new Alias(group, contactEmail, pending.getDisplayName());
      alias.save();

      JPAPlugin.closeTx(false);
    }
  }

  public void contactListInitCompleted(MsnMessenger messenger) {
    MsnOwner owner = messenger.getOwner();
    Logger.info("(%s) Initital contact list complete", owner.getEmail());

    JPAPlugin.startTx(false);
    Group group = Group.find("byEmail", owner.getEmail().getEmailAddress())
        .first();
    List<Alias> aliases = Alias.find("byGroup", group).fetch();
    for (Alias alias : aliases) {
      messenger.renameFriend(Email.parseStr(alias.email), alias.name);
    }

    MsnContact[] contacts = messenger.getContactList().getContacts();
    for (MsnContact contact : contacts) {
      messenger.unblockFriend(contact.getEmail());
      String email = contact.getEmail().getEmailAddress();
      Alias alias = Alias.find("byGroupAndEmail", group, email).first();
      if (alias == null) {
        alias = new Alias(group, email, contact.getFriendlyName());
        alias.save();
      }
    }
    JPAPlugin.closeTx(false);
    
    owner.setStatus(MsnUserStatus.ONLINE);
  }

  public void contactListSyncCompleted(MsnMessenger messenger) {

  }

  public void contactPersonalMessageChanged(MsnMessenger messenger,
      MsnContact contact) {
  }

  public void contactRemoveCompleted(MsnMessenger messenger,
      MsnContact contact, MsnList list) {
    Email ownerEmail = messenger.getOwner().getEmail();
    Logger.info("(%s) User: %s is removed", ownerEmail, contact.getEmail());
  }

  public void contactRemoveFromGroupCompleted(MsnMessenger messenger,
      MsnContact contact, MsnGroup group) {
  }

  public void contactRemovedMe(MsnMessenger messenger, MsnContact contact) {
    Email ownerEmail = messenger.getOwner().getEmail();
    Logger.info("(%s) User: %s removes me from their list", ownerEmail,
        contact.getEmail());
    messenger.removeFriend(contact.getEmail(), false);

    JPAPlugin.startTx(false);

    Alias alias = Alias.find("byEmail", contact.getEmail().getEmailAddress())
        .first();
    alias.delete();

    JPAPlugin.closeTx(false);
  }

  public void contactStatusChanged(MsnMessenger messenger, MsnContact contact) {
  }

  public void groupAddCompleted(MsnMessenger arg0, MsnGroup arg1) {
  }

  public void groupRemoveCompleted(MsnMessenger arg0, MsnGroup arg1) {
  }

  public void ownerDisplayNameChanged(MsnMessenger arg0) {
  }

  public void ownerStatusChanged(MsnMessenger arg0) {
  }

}
