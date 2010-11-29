package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import notifiers.Mails;

import play.Play;
import play.db.jpa.Model;
import play.libs.Codec;
import play.libs.Mail;
import play.mvc.Http.Request;
import play.mvc.Router;

@Entity
public class User extends Model {

  public static enum Role {
    ADMIN, USER;
  }

  public String username;
  public String hash;
  public String email;
  @Enumerated(EnumType.ORDINAL)
  public Role role;
  public Boolean active;
  public String code;

  public User(String username, String password, String email) {
    this.username = username;
    this.hash = Codec.hexSHA1(password);
    this.email = email;
    this.active = false;
    this.role = Role.USER;
  }

  public Boolean isOverQuota() {
    Quota quota = quota();
    return quota.groupCreate >= quota.groupLimit && quota.groupLimit >= 0;
  }

  public Quota quota() {
    return Quota.find("byUser", this).first();
  }

  public void verify(String code) {
    if (this.code.equals(code)) {
      this.active = true;
      this.code = "";
      save();
    }
  }

  public static boolean authenticate(String username, String password) {
    boolean result = false;

    User user = User.find("byUsername", username).first();
    if (user != null) {
      String hash = Codec.hexSHA1(password);
      result = hash.equals(user.hash) && user.active;
    }

    return result;
  }

  public static boolean register(String username, String password, String email) {
    Boolean result = false;

    Date today = new Date();
    Long current = today.getTime();
    Long total = User.count("byUsername", username);
    if (total == 0) {
      User user = new User(username, password, email);
      user.code = Codec.hexSHA1(Long.toHexString(current));
      user.save();

      Quota quota = new Quota(user, 1);
      quota.save();

      Mails.verify(user);

      result = true;
    } else {
      User user = User.find("byUsername", username).first();
      if (!user.active) {
        user.email = email;
        user.hash = Codec.hexSHA1(password);
        user.code = Codec.hexSHA1(Long.toHexString(current));
        user.save();

        Mails.verify(user);

        result = true;
      }
    }

    return result;
  }

  public static User fromUsername(String username) {
    return User.find("byUsername", username).first();
  }

}
