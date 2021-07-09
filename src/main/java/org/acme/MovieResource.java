package org.acme;

import org.acme.amqp.config.AmqpTemplate;
import org.acme.amqp.config.QueueEnumConfig;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public class MovieResource {

    @Inject
    AmqpTemplate producer;

    @POST
    public Response send(String movie) {
        producer.send(movie, QueueEnumConfig.SAMPLE);
        // Return an 202 - Accepted response.
        return Response.accepted().build();
    }

    @POST
    @Path("t")
    public Response send2(String movie) {
        producer.send(movie, QueueEnumConfig.THOR);
        // Return an 202 - Accepted response.
        return Response.accepted().build();
    }
}