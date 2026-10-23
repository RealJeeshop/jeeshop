package org.rembx.jeeshop.rest

import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper

/** Avoid logging stack traces for application WebApplicationException instances. */
class WebApplicationExceptionMapper : ExceptionMapper<WebApplicationException> {
    override fun toResponse(exception: WebApplicationException): Response = exception.response
}
