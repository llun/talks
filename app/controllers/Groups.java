package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.Alias;
import models.Group;
import models.MessageLog;
import models.User;
import net.sf.jml.MsnContact;
import net.sf.jml.MsnUserStatus;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import play.templates.JavaExtensions;

@With(Secure.class)
public class Groups extends Controller {

  public static void index() {
    User user = User.fromUsername(Security.connected());
    List<Group> groups = Group.find("byOwner", user).fetch();

    ArrayList<Group> liveGroup = new ArrayList<Group>();
    List<Alias> aliases = Alias.find("byEmail", user.email).fetch();
    for (Alias alias : aliases) {
      liveGroup.add(alias.group);
    }

    render(user, groups, liveGroup);
  }

  public static void contacts(@Required Long groupID) {
    if (Validation.hasErrors()) {
      index();
    } else {
      Group group = Group.findById(groupID);
      MsnContact contacts[] = group.contacts();
      ArrayList<Alias> aliases = new ArrayList<Alias>();
      for (MsnContact contact : contacts) {
        String display = contact.getFriendlyName();
        if (display.length() > 20) {
          display = display.substring(0, 20).concat("...");
        }
        Alias alias = new Alias(group, contact.getEmail().getEmailAddress(),
            display);
        aliases.add(alias);
      }
      render(group, aliases);
    }
  }

  public static void add(@Required String display, @Required String email,
      @Required String password) throws Throwable {
    if (Validation.hasErrors()) {
      index();
    } else {
      User owner = User.fromUsername(Security.connected());

      Group group = Group.add(display, email, password, owner);
      if (group != null) {
        group.login();
      }

      index();
    }
  }

  public static void remove(@Required Long groupID) {
    if (!Validation.hasErrors()) {
      Group group = Group.findById(groupID);
      if (group != null) {
        group.remove();
      }
    }

    index();
  }

  public static void log(@Required Long groupID, @Required Integer page) {
    if (Validation.hasErrors()) {
      index();
    } else {
      Group group = Group.findById(groupID);
      Map<String, List<MessageLog>> logs = group.log(page, true);
      Long totalPage = group.totalLogPage() / 10 + 1;
      render(group, logs, page, totalPage);
    }
  }

  public static void toggle(@Required Long groupID) {
    if (!Validation.hasErrors()) {
      Group group = Group.findById(groupID);
      if (group != null) {
        if (group.status().equals(MsnUserStatus.ONLINE)) {
          group.logout();
        } else {
          group.login();
        }
      }
    }

    index();
  }

}
