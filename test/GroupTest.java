import junit.framework.Assert;
import models.Group;
import models.Quota;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class GroupTest extends UnitTest {

  @Before
  public void setup() {
    Quota.deleteAll();
    Group.deleteAll();
    User.deleteAll();
    
    Fixtures.load("data.yml");
  }
  
  @Test
  public void testAdd() {
    User.register("test", "password", "test@email.com");
    User user = User.fromUsername("test");
    
    Group group = Group.add("test", "group1@hotmail.com", "1password;", user);
    Assert.assertNotNull(group);
    
    group = group.add("test again", "group2@hotmail.com", "1password", user);
    Assert.assertNull(group);
    
    User admin = User.fromUsername("admin");
    group = group.add("test admin", "group1@hotmail.com", "1password", admin);
    Assert.assertNotSame(admin, group.owner);
    
    group = group.add("test admin 2", "group3@hotmail.com", "1password", admin);
    Assert.assertNotNull(group);
    Assert.assertSame(admin, group.owner);
    
    group = group.add("test admin 3", "group4@hotmail.com", "1password", admin);
    Assert.assertNotNull(group);
  }

}
