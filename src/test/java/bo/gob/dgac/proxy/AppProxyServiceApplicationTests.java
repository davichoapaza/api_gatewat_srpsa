package bo.gob.dgac.proxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


/*@SpringBootTest(properties = {
	    "spring.security.oauth2.client.registration.keycloak.client-id=test",
	    "spring.security.oauth2.client.provider.keycloak.issuer-uri=http://192.168.25.17:8080/realms/test"
	})*/

@SpringBootTest
//@ActiveProfiles("test")
class AppProxyServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
