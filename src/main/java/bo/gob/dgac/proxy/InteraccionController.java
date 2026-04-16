package bo.gob.dgac.proxy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpResponse;
import java.net.URI;

/**
 * @author David APAZA
 * @version 1.0
 * Encargado de realizar la gestion de sesiones, 
 * Obtiene el perfil del usuario logeado y logout
 * 
 */
@RestController
@RequestMapping("/api/proxy")
public class InteraccionController {
	
	@Value("${app.frontend-url}")
	private String frontendUrl;

	@Value("${app.keycloak-url}")
	private String keycloakUrl;
	
    @GetMapping("/usuario-info")
    public Mono<Map<String, Object>> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        return Mono.just(principal.getAttributes());
    }
    
     @GetMapping("/logout")
     public Mono<Void> logout(ServerWebExchange exchange) {
        

        return exchange.getSession()
            .flatMap(session -> session.invalidate()) 
            .then(Mono.defer(() -> {
                

                String keycloakLogoutUrl = keycloakUrl+"/realms/apprpsa/protocol/openid-connect/logout"
                                         + "?post_logout_redirect_uri="+frontendUrl
                                         + "&client_id=backend-proxy-client";
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FOUND); // Código 302
                response.getHeaders().setLocation(URI.create(keycloakLogoutUrl));
                return response.setComplete();
            }));
    }
   
}



