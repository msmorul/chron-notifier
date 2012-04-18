/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chronopolis.notify.db;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.chronopolis.notify.IngestRequest;

/**
 *
 * @author toaster
 */
public class TicketManager {

    private static EntityManagerFactory emf;
    private static final Logger LOG = Logger.getLogger(TicketManager.class);

    public TicketManager() {
        emf = EntityManagerFactoryProducer.get();
    }

    /**
     * 
     * @param ticketId ticket to be update
     * @param description message to set as ticket's status mesage
     * @return false if ticket is not open
     */
    public boolean updateMessage(String ticketId, String description) {
        return setTicketStatus(ticketId, description, Ticket.STATUS_OPEN);
    }

    /**
     * 
     * @param ticketId ticket to be update
     * @param description message to set as ticket's status mesage
     * @return false if ticket is not open
     */
    public boolean completeTicket(String ticketId, String description) {
        return setTicketStatus(ticketId, description, Ticket.STATUS_FINISHED);
    }

    /**
     * 
     * @param ticketId ticket to be update
     * @param description message to set as ticket's status mesage
     * @return false if ticket is not open
     */
    public boolean errorTicket(String ticketId, String description) {
        return setTicketStatus(ticketId, description, Ticket.STATUS_ERROR);
    }

    /**
     * 
     * @param ticketId
     * @return 
     */
    public Ticket getTicket(String ticketId) {
        EntityManager em = emf.createEntityManager();

        try {
            Query q = em.createNamedQuery("Ticket.getByIdentifier");
            q.setParameter("id", ticketId);
            Ticket t = (Ticket) q.getSingleResult();
            LOG.trace("Returning ticket: " + t);
            return t;

        } catch (NoResultException e) {
            LOG.info("Attempt to retrieve non-existent ticket: " + ticketId);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * List all registered tickets
     * 
     * @return all tickets
     */
    public List<Ticket> listAll() {
        EntityManager em = emf.createEntityManager();

        try {
            Query q = em.createNamedQuery("Ticket.listAll");
            return (List<Ticket>) q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * 
     */
    public Ticket createTicket(String account, String space, String item) {
        EntityManager em = emf.createEntityManager();
        try {

            Ticket ticket = new Ticket();

            ticket.setIdentifier(Long.toString(System.currentTimeMillis()));
            ticket.setAccountId(account);
            ticket.setSpaceId(space);
            ticket.setStatus(Ticket.STATUS_OPEN);

            if (item == null || item.isEmpty()) {
                ticket.setRequestType(Ticket.REQUEST_FULL_RESTORE);

            } else {
                ticket.setItemId(item);
                ticket.setRequestType(Ticket.REQUEST_SINGLE_ITEM);
            }


            em.getTransaction().begin();
            em.persist(ticket);
            LOG.debug("Registered new retrieve ticket: " + ticket.getIdentifier());
            em.getTransaction().commit();

            return ticket;
        } finally {
            em.close();
        }

    }

    /**
     * Create new ticket and register w/ db. If the request is in error, ticket status
     * will be set to error and status message will be set to error lines in manifest
     * 
     * @param ir transfer request
     * @return new ticket
     */
    public Ticket createTicket(IngestRequest ir,String sourceDigest, String targetDigest) {
        EntityManager em = emf.createEntityManager();

        try {
            Ticket ticket = new Ticket();

            ticket.setIdentifier(Long.toString(System.currentTimeMillis()));
            ticket.setAccountId(ir.getAccount());
            ticket.setSpaceId(ir.getSpace());
            ticket.setRequestType(Ticket.REQUEST_INGEST);

//            ticket.setManifestValues(ir.getManifest());
            if (sourceDigest == null || sourceDigest.equals(targetDigest))
            {
                ticket.setStatus(Ticket.STATUS_ERROR);
                ticket.setStatusMessage("Digest empty or does not match. Header digest: " + sourceDigest + " Computed: " + targetDigest);
            }
            else if (ir.hasErrors()) {
                ticket.setStatus(Ticket.STATUS_ERROR);
                ticket.setStatusMessage(createErrorList(ir));
            } else {
                ticket.setStatusMessage("Processing request sent");
                ticket.setManifest(manifestToString(ir));
                ticket.setStatus(Ticket.STATUS_OPEN);
            }

            em.getTransaction().begin();
            em.persist(ticket);
            LOG.debug("Registered new ticket: " + ticket.getIdentifier() + " errors: " + ir.hasErrors());
            em.getTransaction().commit();

            return ticket;
        } finally {
            em.close();
        }
    }

    private String createErrorList(IngestRequest ir) {
        StringBuilder sb = new StringBuilder("The folling manifest errors were detected \n");
        for (String s : ir.getErrors()) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    private String manifestToString(IngestRequest ir) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : ir.getManifest().entrySet()) {
                sb.append(entry.getValue()).append(" ").append(entry.getKey()).append("\n");
            }
        return sb.toString();
    }
    
    private boolean setTicketStatus(String ticketId, String description, int state) {
        EntityManager em = emf.createEntityManager();


        try {
            Query q = em.createNamedQuery("Ticket.getByIdentifier");
            q.setParameter("id", ticketId);

            Ticket t = (Ticket) q.getSingleResult();

            // only allow open ticket updates
            if (t.getStatus() != Ticket.STATUS_OPEN) {
                LOG.info("Attempt to update state on closed or error-ed ticket " + ticketId + " description: " + description);
                return false;
            }

            t.setStatus(state);
            t.setStatusMessage(description);
            em.getTransaction().begin();
            em.merge(t);
            em.getTransaction().commit();
            LOG.debug("DB update complete for: " + ticketId);
            return true;
        } catch (NoResultException e) {
            LOG.info("Request to update non-existent ticket: " + ticketId);
            return false;
        } finally {
            em.close();
        }
    }
}
