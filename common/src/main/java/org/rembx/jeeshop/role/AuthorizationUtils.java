package org.rembx.jeeshop.role;

import javax.ejb.SessionContext;

/**
 * Created by remi on 24/06/14.
 */
public class AuthorizationUtils {

    public static boolean isAdminUser(SessionContext sessionContext) {
        return sessionContext != null && sessionContext.getCallerPrincipal()!=null
                && sessionContext.isCallerInRole(JeeshopRoles.ADMIN);
    }

}
