package org.acme;

import org.acme.resteasyjackson.MessageService;

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
    MessageService producer;

    @POST
    public Response send(String movie) {
        producer.send(movie);
        // Return an 202 - Accepted response.
        return Response.accepted().build();
    }
}