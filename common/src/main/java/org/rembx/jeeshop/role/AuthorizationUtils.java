package org.rembx.jeeshop.role;

import javax.ejb.SessionContext;

/**
 * Created by remi on 24/06/14.
 */
public class AuthorizationUtils {

    public static boolean isAdminUser(SessionContext sessionContext) {
        return sessionContext != null && sessionContext.getCallerPrincipal()!=null
                && sessionContext.getCallerPrincipal().getName().equals(JeeshopRoles.ADMIN);
    }

    public static boolean isNotAdminUser(SessionContext sessionContext){
        return !isAdminUser(sessionContext);
    }

    public static boolean isEndUser(SessionContext sessionContext) {
        return sessionContext != null && sessionContext.getCallerPrincipal()!=null
                && sessionContext.getCallerPrincipal().getName().equals(JeeshopRoles.USER);
    }

}
