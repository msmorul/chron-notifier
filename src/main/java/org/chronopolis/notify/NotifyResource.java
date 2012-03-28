/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chronopolis.notify;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.chronopolis.notify.db.Ticket;
import org.chronopolis.notify.db.TicketManager;

/**
 * Core put / get resources for notifying chronopolis of push or pull requests
 *
 * @author toaster
 */
@Path("notify")
public final class NotifyResource {

    private TicketManager tm = new TicketManager();
    private static final Logger LOG = Logger.getLogger(NotifyResource.class);

    public NotifyResource() {
    }

    /**
     * Request retrieval of a complete Bag
     * 
     * @param accountId
     * @param spaceId
     * @param request
     * @return ticket to retrieve transfer status
     **/
    @GET
    @Path("{accountId}/{spaceId}")
    @Produces("text/plain")
    public String requestBag(@PathParam("accountId") String accountId,
            @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest request) {
        try {

            NDC.push("S" + accountId);
            LOG.info("Request to retrieve item. ID: " + spaceId + "  Account: " + accountId);


            Ticket ticket = tm.createTicket(accountId, spaceId, null);
            MailUtil.sendMessage(ticket, null);


            return ticket.getIdentifier();
        } finally {
            LOG.info("Completed request to retrieve item ID: " + spaceId + " Account: " + accountId);
            NDC.pop();

        }
    }

    /**
     * retieve a single item from chronopolis
     * 
     * @param accountId
     * @param spaceId
     * @param contentId
     * @param request
     * @return ticket
     */
    @GET
    @Path("{accountId}/{spaceId}/{contentId}")
    @Produces("text/plain")
    public String requestBag(@PathParam("accountId") String accountId,
            @PathParam("spaceId") String spaceId,
            @PathParam("contentId") String contentId,
            @Context HttpServletRequest request) {

        try {

            NDC.push("I" + accountId);
            LOG.info("Request to retrieve item. ID: " + spaceId + "/" + contentId + "  Account: " + accountId);

            Ticket ticket = tm.createTicket(accountId, spaceId, contentId);
            MailUtil.sendMessage(ticket, null);
            return ticket.getIdentifier();
        } finally {
            LOG.info("Completed request to retrieve item ID: " + spaceId + "/" + contentId + " Account: " + accountId);
            NDC.pop();

        }

    }

    /**
     * Notify Chronopolis a manifest is available for transfer
     *  response status set as follows:
     *      SC_BAD_REQUEST malformed manifest
     *      SC_ACCEPTED got, parsed, and e-mailed response
     * 
     * @param accountId
     * @param spaceId
     * @return ticket id to be used for status updates
     */
    @PUT
    @Path("{accountId}/{spaceId}")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putManifest(@PathParam("accountId") String accountId,
            @PathParam("spaceId") String spaceId, @Context HttpHeaders headers,
            @Context HttpServletRequest request, @Context HttpServletResponse response) {
        try {

            NDC.push("R" + accountId);
            LOG.info("Request to receive space. ID: " + spaceId + " Account: " + accountId);
            // String digest = headers.getRequestHeader("Content-MD5") TODO extract http headers
            InputStream is = request.getInputStream();
            IngestRequest ir = new IngestRequest(accountId, spaceId);
            MessageDigest md = MessageDigest.getInstance("MD5");
            ir.readStream(is, md);

            Ticket ticket = tm.createTicket(ir);
            MailUtil.sendMessage(ticket, ir);


            if (ir.hasErrors()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            }

            //TODO: update response to reasonable value
            response.setHeader("Retry-After", "120");
            return ticket.getIdentifier();

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            LOG.error("Error reading client supplied manifest stream ", e);
            return "";
        } catch (NoSuchAlgorithmException e) {
            // should never happen
            LOG.error(e);
            throw new RuntimeException(e);

        } finally {
            LOG.info("Completed request to receive space. ID: " + spaceId + " Account: " + accountId);
            NDC.pop();

        }

    }
}
