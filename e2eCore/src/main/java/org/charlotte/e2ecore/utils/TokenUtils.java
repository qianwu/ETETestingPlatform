package org.charlotte.e2ecore.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SecurityContextUtils is used to get username and roles to set created by, last updated by fields.
 */
@Component
public class TokenUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenUtils.class);

    private static final String ANONYMOUS = "anonymous";

    private TokenUtils() {
    }

    public static String getUserName() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        String username = ANONYMOUS;

        if (null != authentication) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                username = springSecurityUser.getUsername();

            } else if (authentication.getPrincipal() instanceof String) {
                username = (String) authentication.getPrincipal();

            } else {
                LOGGER.debug("User details not found in Security Context");
            }
        } else {
            LOGGER.debug("Request not authenticated, hence no user name available");
        }

        return username;
    }

    public static Map<String, Object> getUserClaim() {
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = servletRequestAttributes.getRequest();
//        final Principal userPrincipal = request.getUserPrincipal();
//        Map<String, Object> userClaims = Maps.newHashMap();
//        if (userPrincipal instanceof KeyCloakJwtAuthenticationToken) {
//            KeyCloakJwtAuthenticationToken token = (KeyCloakJwtAuthenticationToken) userPrincipal;
////            AccessToken accessToken = token.getAccessToken();
//            userClaims = token.getToken().getClaims();
//            LOGGER.info("claims={}", JsonUtils.prettyPrint(String.valueOf(userClaims)));
//        }
//
////        System.out.println(userClaims.get("email"));
////        System.out.println(userClaims.get("name"));
//        return userClaims;

        KeyCloakJwtAuthenticationToken token = (KeyCloakJwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = token.getToken().getClaims();
        return claims;
    }

    public static Set<String> getUserRoles() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Set<String> roles = new HashSet<>();

        if (null != authentication) {
            authentication.getAuthorities()
                    .forEach(e -> roles.add(e.getAuthority()));
        }
        return roles;
    }
}