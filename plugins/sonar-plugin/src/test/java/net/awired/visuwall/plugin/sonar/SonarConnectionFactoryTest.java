package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertFalse;
import net.awired.visuwall.api.exception.ConnectionException;

import org.junit.Test;

public class SonarConnectionFactoryTest {

    SonarConnectionFactory sonarConnectionFactory = new SonarConnectionFactory();

    @Test(expected = NullPointerException.class)
    public void can_t_pass_null_to_create_method() throws ConnectionException {
        sonarConnectionFactory.create(null);
    }

    @Test
    public void should_create_sonar_connection() throws ConnectionException {
        SonarConnection connection = sonarConnectionFactory.create("http://sonar:9000");
        assertFalse(connection.isClosed());
    }

}
