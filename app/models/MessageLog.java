package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

@Entity
public class MessageLog extends Model {

  public String groupEmail;
  public String contactEmail;

  @Lob
  @Column(length = 30000)
  public String message;
  public Date created;

  public MessageLog(Group group, String email, String message) {
    this.groupEmail = group.email;
    this.contactEmail = email;
    this.message = message;

    this.created = new Date();
  }

  public static void log(Group group, String email, String message) {
    MessageLog log = new MessageLog(group, email, message);
    log.save();
  }

  public static MessageLog[] last(int count) {
    List<MessageLog> logs = MessageLog.find("order by timestamp").fetch(count);
    return logs.toArray(new MessageLog[logs.size()]);
  }

}
