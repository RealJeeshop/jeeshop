package org.rembx.jeeshop.rest

import jakarta.ws.rs.core.Response

class WebApplicationException(status: Response.Status) : jakarta.ws.rs.WebApplicationException(status)
