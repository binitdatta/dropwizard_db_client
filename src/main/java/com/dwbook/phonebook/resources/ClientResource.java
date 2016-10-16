package com.dwbook.phonebook.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dwbook.phonebook.representations.Contact;

@Produces(MediaType.APPLICATION_JSON)
@Path("/client/")
public class ClientResource {

    private Client client;

    public ClientResource(Client client) {
        this.client = client;
    }
    public ClientResource(){
    	
    }
    
    @GET
    @Path("/contact/")
    public Response showContacts()
    {
    	WebTarget webTarget = client.target("http://localhost:8080/contact");
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        @SuppressWarnings("rawtypes")
        String employees = response.readEntity(String.class);
        return Response.ok(employees).build();
    }
    
    @GET
    @Path("showContact")
    public Response showContact(@QueryParam("id") int id) {
    	WebTarget contactResource = client.target("http://localhost:8080/contact/" + id);
        Invocation.Builder invocationBuilder =  contactResource.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        @SuppressWarnings("rawtypes")
        String c = response.readEntity(String.class);
        return Response.ok(c).build();
    }

    @GET
    @Path("newContact")
    public Response newContact(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @QueryParam("phone") String phone) {
    	WebTarget contactResource = client.target("http://localhost:8080/contact");
        Invocation.Builder invocationBuilder =  contactResource.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.post(Entity.json(new Contact(0, firstName, lastName, phone)));
        if (response.getStatus() == 201) {
            // Created
            return Response.status(302).entity("The contact was created successfully! The new contact can be found at " + response.getHeaders().getFirst("Location")).build();
        } else {
            // Other Status code, indicates an error
            return Response.status(422).entity(response.readEntity(String.class)).build();
        }
    }

    @GET
    @Path("updateContact")
    public Response updateContact(@QueryParam("id") int id, @QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @QueryParam("phone") String phone) {
    	WebTarget contactResource = client.target("http://localhost:8080/contact/" + id);
        Invocation.Builder invocationBuilder =  contactResource.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.put(Entity.json(new Contact(0, firstName, lastName, phone)));

        if (response.getStatus() == 200) {
            // Created
            return Response.status(302).entity("The contact was updated successfully!").build();
        } else {
            // Other Status code, indicates an error
            return Response.status(422).entity(response.readEntity(String.class)).build();
        }
    }

    @GET
    @Path("deleteContact")
    public Response deleteContact(@QueryParam("id") int id) {
    	WebTarget contactResource = client.target("http://localhost:8080/contact/" + id);
        Invocation.Builder invocationBuilder =  contactResource.request(MediaType.APPLICATION_JSON);

        invocationBuilder.delete();
        return Response.noContent().entity("Contact was deleted!").build();
    }
}
