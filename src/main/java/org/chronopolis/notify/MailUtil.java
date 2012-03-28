/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chronopolis.notify;

import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import org.chronopolis.notify.db.Ticket;

/**
 * yeah, fugly
 * @author toaster
 */
public class MailUtil implements ServletContextListener {
    
    private static String mailserver;
    private static String fromAddr;
    private static String toAddr;
    private static final Logger LOG = Logger.getLogger(MailUtil.class);
    
    public static void sendMessage(Ticket t, IngestRequest ir)  {
        Properties props = new Properties();
        props.put("mail.smtp.host", mailserver);
        Session s = Session.getInstance(props, null);
        
        try
        {
        InternetAddress from = new InternetAddress(fromAddr);
        InternetAddress to = new InternetAddress(toAddr);
        
        MimeMessage message = new MimeMessage(s);
        message.setFrom(from);
        message.addRecipient(Message.RecipientType.TO, to);
        
        StringBuilder sb = new StringBuilder("Transfer Request: ");
        sb.append(t.getIdentifier());
        switch (t.getRequestType()) {
            case Ticket.REQUEST_FULL_RESTORE:
                sb.append("Full Bag Restore");
                break;
            case Ticket.REQUEST_INGEST:
                sb.append("Create Bag");
                break;
            case Ticket.REQUEST_SINGLE_ITEM:
                sb.append("Single Item Restore");
                break;
            default:
                sb.append("Unknown ");
                sb.append(t.getRequestType());
        }
        message.setSubject(sb.toString());
        
        
        message.setText(buildMsg(t, ir));
        
        Transport.send(message);
        }
        catch (MessagingException e)
        {
            LOG.error("Error sending request",e);
            throw new RuntimeException(e);
                    
        }
    }
    
    private static String buildMsg(Ticket t, IngestRequest ir) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ticket: ");
        sb.append(t.getIdentifier());
        sb.append("\nRequest Type: ");
        switch (t.getRequestType()) {
            case Ticket.REQUEST_FULL_RESTORE:
                sb.append("Full Restore");
                break;
            case Ticket.REQUEST_INGEST:
                sb.append("Ingest");
                break;
            case Ticket.REQUEST_SINGLE_ITEM:
                sb.append("Single Item");
                break;
            default:
                sb.append("Unknown ");
                sb.append(t.getRequestType());
        }
        
        sb.append("\n\nSpace: ");
        sb.append(t.getSpaceId());
        sb.append("\nAccount: ");
        sb.append(t.getAccountId());
        sb.append("\nItem: ");
        sb.append(t.getItemId());
        if (t.getStatus() == Ticket.STATUS_ERROR) {
            sb.append("\n\nErrors: \n");
            sb.append(t.getStatusMessage());
        }
        if (t.getRequestType() == Ticket.REQUEST_INGEST) {
            sb.append("\n\n-----------------\nTransfer Manifest\n\n");
            
            for (Map.Entry<String, String> entry : ir.getManifest().entrySet()) {
                sb.append(entry.getValue()).append(": ").append(entry.getKey()).append("\r\n");
            }
        }
        return sb.toString();
        
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        mailserver = sce.getServletContext().getInitParameter("server");
        fromAddr = sce.getServletContext().getInitParameter("fromAddr");
        toAddr = sce.getServletContext().getInitParameter("toAddr");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
