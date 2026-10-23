package org.rembx.jeeshop.user.domain

import org.rembx.jeeshop.stereotype.Repository

@Repository
interface RoleRepository {
    fun findByName(name: RoleName): Role?
}
