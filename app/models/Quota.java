package models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

@Entity
public class Quota extends Model {

  @OneToOne
  @JoinColumn(name="USER_ID")
  public User user;
  public Integer groupCreate;
  public Integer groupLimit;
  
  public Quota(User user, Integer limit) {
    this.user = user;
    this.groupLimit = limit;
    this.groupCreate = 0;
  }
  
}
