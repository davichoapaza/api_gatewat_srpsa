package bo.gob.dgac.proxy;

import java.util.Map;

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



@RestController
@RequestMapping("/api/proxy")
public class InteraccionController {
    @GetMapping("/user-info")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        // Retorna los datos del ciudadano que vienen de Keycloak/AGETIC
        return principal.getAttributes();
    }
   
    
    
/*   esto no fuciona pq esto no funciona en programacion reactiva
    @GetMapping("/logout") // Esto completa la ruta: /api/v1/interaccion/logout
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
         	System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDD  ");
             // 1. Invalidar la sesión en Spring Boot (borra la cookie JSESSIONID)
             SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
             logoutHandler.logout(request, null, null);

             // 2. Redirigir a Keycloak para el cierre de sesión global
             String keycloakLogoutUrl = "http://192.168.25.17:8080/realms/apprpsa/protocol/openid-connect/logout"
                                      + "?post_logout_redirect_uri=http://192.168.25.17:4200"
                                      + "&client_id=backend-proxy-client";
                    //  Enviar la orden de redirección al navegador
             response.sendRedirect(keycloakLogoutUrl);
         }
    
    //http://192.168.25.17:7070/api/v1/interaccion/logout
    */
    
     @GetMapping("/logout")
     public Mono<Void> logout(ServerWebExchange exchange) {
        System.out.println("Ejecutando Logout Reactivo... DDDDDDDDDDDDDDDDDDDDDDDDD");

        // 1. Invalidar la sesión en WebFlux (esto borra la cookie de sesión reactiva)
        return exchange.getSession()
            .flatMap(session -> session.invalidate()) 
            .then(Mono.defer(() -> {
                
                // 2. Preparar la URL de Keycloak para el cierre de sesión global
                String keycloakLogoutUrl = "http://192.168.25.17:8080/realms/apprpsa/protocol/openid-connect/logout"
                                         + "?post_logout_redirect_uri=http://192.168.25.17:4200"
                                         + "&client_id=backend-proxy-client";

                // 3. Configurar la redirección en la respuesta reactiva
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FOUND); // Código 302
                response.getHeaders().setLocation(URI.create(keycloakLogoutUrl));

                // Finalizar el procesamiento de la respuesta
                return response.setComplete();
            }));
    }
}



