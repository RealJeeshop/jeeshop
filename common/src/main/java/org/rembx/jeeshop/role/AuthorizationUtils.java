package org.rembx.jeeshop.role;

import javax.ws.rs.core.SecurityContext;


/**
 * Created by remi on 24/06/14.
 */
public class AuthorizationUtils {

    public static boolean isAdminUser(SecurityContext sessionContext) {
        return sessionContext != null && sessionContext.getUserPrincipal() != null
                && sessionContext.isUserInRole(JeeshopRoles.ADMIN);
    }

    public static  boolean isOwner(SecurityContext securityContext, String owner) {
        return securityContext != null && securityContext.getUserPrincipal() != null
                && owner != null && securityContext.isUserInRole(JeeshopRoles.STORE_ADMIN)
                && owner.equals(securityContext.getUserPrincipal().getName());
    }

}
