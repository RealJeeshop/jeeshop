package org.rembx.jeeshop.role

import jakarta.ws.rs.core.SecurityContext

object AuthorizationUtils {
    @JvmStatic
    fun isAdminUser(sessionContext: SecurityContext?): Boolean =
        sessionContext?.userPrincipal != null && sessionContext.isUserInRole(JeeshopRoles.ADMIN)

    @JvmStatic
    fun isOwner(securityContext: SecurityContext?, owner: String?): Boolean =
        securityContext?.userPrincipal != null &&
            owner != null &&
            securityContext.isUserInRole(JeeshopRoles.STORE_ADMIN) &&
            owner == securityContext.userPrincipal.name
}
