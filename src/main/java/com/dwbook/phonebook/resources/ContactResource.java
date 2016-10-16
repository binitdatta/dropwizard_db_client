package com.dwbook.phonebook.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.skife.jdbi.v2.DBI;

import com.dwbook.phonebook.dao.ContactDAO;
import com.dwbook.phonebook.representations.Contact;

@Path("/contact")
@Produces(MediaType.APPLICATION_JSON)
public class ContactResource {

    private final ContactDAO contactDao;
    private final Validator validator;

    public ContactResource(DBI jdbi, Validator validator) {
        contactDao = jdbi.onDemand(ContactDAO.class);
        this.validator = validator;
    }

    @GET
    public Response getContacts() {
        // retrieve information about the contact with the provided id
        List<Contact> contacts = contactDao.getContacts();
        return Response
                .ok(contacts)
                .build();
    }
    
    @GET
    @Path("/{id}")
    public Response getContact(@PathParam("id") int id) {
        // retrieve information about the contact with the provided id
        Contact contact = contactDao.getContactById(id);
        return Response
                .ok(contact)
                .build();
    }

    @POST
    public Response createContact(Contact contact) throws URISyntaxException {
        // Validate the contact's data
    	if (contact != null && contact.getFirstName() != null){
    		System.out.println("Contact incoming fn"+contact.getFirstName());
    	}
    	
    	if (contact != null && contact.getLastName() != null){
    		System.out.println("Contact incoming ln"+contact.getLastName());
    	}
    	
    	if (contact != null && contact.getPhone() != null){
    		System.out.println("Contact incoming phone"+contact.getPhone());
    	}
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        // Are there any constraint violations?
        if (violations.size() > 0) {
            // Validation errors occurred
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<Contact> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity(validationMessages)
                    .build();
        } else {
            // OK, no validation errors
            // Store the new contact
            int newContactId = contactDao.createContact(contact.getFirstName(),
                    contact.getLastName(), contact.getPhone());
            return Response.created(new URI(String.valueOf(newContactId))).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteContact(@PathParam("id") int id) {
        // delete the contact with the provided id
        contactDao.deleteContact(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    public Response updateContact(@PathParam("id") int id, Contact contact) {
        // Validate the updated data
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        // Are there any constraint violations?
        if (violations.size() > 0) {
            // Validation errors occurred
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<Contact> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity(validationMessages)
                    .build();
        } else {
			// No errors
            // update the contact with the provided ID
            contactDao.updateContact(id, contact.getFirstName(),
                    contact.getLastName(), contact.getPhone());
            return Response.ok(
                    new Contact(id, contact.getFirstName(), contact.getLastName(),
                            contact.getPhone())).build();
        }
    }

}
