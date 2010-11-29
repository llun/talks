import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import models.Group;
import models.User;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {

  boolean load = false;

  public void doJob() {

    if (User.count() == 0) {
      Logger.info("Loading initial data");
      Fixtures.load("data.yml");

      Logger.info("Load %d user(s)", User.count());
    }

    if (!load) {
      Logger.info("Clean upload directory");
      File file = new File(Play.configuration.getProperty("talks.upload"));
      try {
        FileUtils.deleteDirectory(file);
      } catch (IOException e) {
        Logger.error(e, "Can't clean upload directory");
      }

      Logger.info("Logging in all groups");
      List<Group> groups = Group.all().fetch();
      for (Group group : groups) {
        group.login();
      }
      load = true;
    }

  }

}
