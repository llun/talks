import junit.framework.Assert;
import models.Group;
import models.Quota;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserTest extends UnitTest {

  @Before
  public void setup() {
    Quota.deleteAll();
    Group.deleteAll();
    User.deleteAll();

    Fixtures.load("data.yml");
  }

  @Test
  public void testRegister() {
    Boolean result = User.register("sample", "password", "sample@email.com");
    Assert.assertTrue(result);

    result = User.register("sample", "anotherPassword", "sample@email.com");
    Assert.assertFalse(result);
  }

  @Test
  public void testAuthenticate() {
    Boolean result = User.authenticate("admin", "password");
    Assert.assertTrue(result);

    result = User.authenticate("admin", "wrongPassword");
    Assert.assertFalse(result);

    result = User.authenticate("nouser", "password");
    Assert.assertFalse(result);
  }

}
