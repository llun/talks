package models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

@Entity(name="IMAlias")
public class Alias extends Model {

  @OneToOne
  @JoinColumn(name = "GROUP_ID")
  public Group group;
  public String email;
  public String name;

  public Alias(Group group, String email, String name) {
    this.group = group;
    this.email = email;
    this.name = name;
  }

}
