package msn;

import java.io.File;
import java.util.Date;

import net.sf.jml.MsnContact;
import net.sf.jml.MsnFileTransfer;
import net.sf.jml.MsnOwner;
import net.sf.jml.MsnSwitchboard;
import net.sf.jml.event.MsnFileTransferListener;
import play.Logger;
import play.Play;

public class FileTransferListener implements MsnFileTransferListener {

  public static final FileTransferListener instance = new FileTransferListener();

  public void fileTransferFinished(MsnFileTransfer fileTransfer) {
    Messenger messenger = (Messenger) fileTransfer.getMessenger();
    MsnOwner owner = fileTransfer.getMessenger().getOwner();
    Logger.info("%s is received file.", owner.getEmail());

    String rootURL = Play.configuration.getProperty("talks.url");
    MsnContact[] contacts = fileTransfer.getMessenger().getContactList()
        .getContacts();
    for (MsnContact contact : contacts) {
      if (!contact.getEmail().equals(fileTransfer.getContact().getEmail())) {
        messenger.sendText(contact.getEmail(), String.format(
            "%s send %s to group: %s", fileTransfer.getContact()
                .getFriendlyName(), fileTransfer.getFile().getName(), String
                .format("%s/%s", rootURL, fileTransfer.getFile().getPath())));
      }
    }
  }

  public void fileTransferProcess(MsnFileTransfer fileTransfer) {
  }

  public void fileTransferRequestReceived(MsnFileTransfer fileTransfer) {
    fileTransfer.cancel();
  }

  public void fileTransferStarted(MsnFileTransfer fileTransfer) {
    Logger.info("%s is receiving file.", fileTransfer.getMessenger().getOwner()
        .getEmail());

    MsnOwner owner = fileTransfer.getMessenger().getOwner();

    String uploadRoot = Play.configuration.getProperty("talks.upload");
    int hash = (owner.getEmail().hashCode() % 26);
    File uploadPath = new File(String.format("%s/%c/%s/%s/%s/%s", uploadRoot,
        'A' + hash, owner.getEmail(), new Date().getTime()));
    if (!uploadPath.exists()) {
      if (uploadPath.mkdirs()) {
        fileTransfer.setFile(new File(uploadPath, fileTransfer.getFile()
            .getName()));
        Logger.info("%s is accepted file.", owner.getEmail());
      } else {
        Logger.info("%s can't create new directory", owner.getEmail());
      }
    } else {
      Logger.info("%s already accepts uploading file.", owner.getEmail());
    }
  }

}
