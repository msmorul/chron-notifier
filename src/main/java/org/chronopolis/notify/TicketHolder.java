/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chronopolis.notify;

import javax.xml.bind.annotation.XmlRootElement;
import org.chronopolis.notify.db.Ticket;

/**
 * Return object for get/put calls.
 * @author toaster
 */
@XmlRootElement
public class TicketHolder {
    private String identifier;

    public TicketHolder() {
    }
    
    public TicketHolder(Ticket t) {
        identifier = t.getIdentifier();
    }
    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
}
