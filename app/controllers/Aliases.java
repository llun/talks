package controllers;

import models.Alias;
import models.Group;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;

public class Aliases extends Controller {

  public static void show(@Required Long groupID, @Required String email) {

    if (Validation.hasErrors()) {
      Groups.contacts(groupID);
    } else {
      Group group = Group.findById(groupID);
      Alias alias = Alias.find("byGroupAndEmail", group, email).first();
      if (alias == null) {
        alias = new Alias(group, email, email);
      }
      render(group, alias);
    }

  }

  public static void update(@Required Long groupID, @Required String email,
      @Required String name) {

    if (Validation.hasErrors()) {
      Validation.keep();
      params.flash();
      show(groupID, email);
    } else {
      Group group = Group.findById(groupID);
      group.renameFriend(email, name);
      
      if (group.owner.username.equals(Security.connected())) {
        Groups.contacts(groupID);
      } else {
        Groups.index();
      }
      

    }

  }

}
