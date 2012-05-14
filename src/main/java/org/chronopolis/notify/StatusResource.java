/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chronopolis.notify;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.chronopolis.notify.db.Ticket;
import org.chronopolis.notify.db.TicketManager;

/**
 *
 * @author toaster
 */
@Path("status")
public class StatusResource {

    private static final Logger LOG = Logger.getLogger(StatusResource.class);
    private TicketManager tm = new TicketManager();

    /**
     * Return list of all ticket.
     * 
     * @return 
     */
    @GET
    @Produces("application/json")
    public List<Ticket> listTickets() {
        return tm.listAll();
    }

    /**
     * response set to 
     *      bad_request for non-existent tickets
     *      200/OK for open tickets
     *      201/CREATED for successfully finished tickets
     *      500/INTERNAL ERROR for requests that errored out
     *      404/NOT_FOUND
     * 
     * @param ticket
     * @param response
     * @return ticket object
     */
    @GET
    @Path("{ticket}")
    @Produces("application/json")
    public Response getStatus(@PathParam("ticket") String ticketId,
            @Context HttpServletResponse response) {
        try {

            NDC.push("T" + ticketId);
            LOG.info("Ticket Request ID: " + ticketId);

            Ticket ticket = tm.getTicket(ticketId);
            if (ticket != null) {

                switch (ticket.getStatus()) {
                    case Ticket.STATUS_OPEN:
                        return Response.status(Status.OK).header("Retry-After", "120").entity(ticket).build();
                    case Ticket.STATUS_FINISHED:
                        return Response.status(Status.CREATED).entity(ticket).build();
                    case Ticket.STATUS_ERROR:
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ticket).build();
                    default:
                        LOG.error("Unknown response case: " + ticket.getStatusMessage());
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ticket).build();
                }

            } else {
                LOG.debug("Returning not-found, ticket ID unknown: " + ticketId);
                return Response.status(Status.NOT_FOUND).build();
            }

        } finally {
            LOG.info("Completed Ticket Request ID: " + ticketId);
            NDC.pop();
        }
    }

    /**
     * Update the running state of a ticket
     * 
     * // TODO: add manifest ingest as form/multipart
     * 
     * @param ticket
     * @param isError
     * @param description
     * @param isFinished
     * @param response 
     */
    @POST
    @Path("{ticket}")
    public void setStatus(@PathParam("ticket") String ticket,
            @FormParam("isError") @DefaultValue(value = "false") boolean isError,
            @FormParam("description") String description,
            @FormParam("isFinished") @DefaultValue(value = "false") boolean isFinished,
            @Context HttpServletResponse response) {

        try {

            NDC.push("U" + ticket);
            LOG.info("Ticket Request ID: " + ticket + " error: " + isError + " finished: " + isFinished);
            if (isError) {
                tm.errorTicket(ticket, description);
            } else if (isFinished) {
                tm.completeTicket(ticket, description);
            } else {
                tm.updateMessage(ticket, description);
            }
        } finally {
            LOG.info("Completed Ticket Request ID: " + ticket);
            NDC.pop();
        }
    }
}
