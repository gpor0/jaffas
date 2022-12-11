import com.github.gpor0.jaffas.endpoints.model.SyncRolePermissions;
import com.github.gpor0.jaffas.security.Security;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

public class ReadYamlTest {

    @Test //for manual testing purposes, more tests should come...
    public void shouldReadSecurities() {
        Security sec = new Security() {
            @Override
            public String getApplicationName() {
                return null;
            }

            @Override
            public List<ApplicationRole> getRoles() {
                return new LinkedList<>();
            }
        };

        SyncRolePermissions syncRolePermissions = sec.readYamlSecurity(ReadYamlTest.class.getClassLoader().getResourceAsStream("petstore.yaml"), true);
        assert syncRolePermissions != null;
    }

}
