/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chronopolis.notify.db;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 *  
 * @author toaster
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ticket.listAll", query = "SELECT t FROM Ticket t"),
    @NamedQuery(name = "Ticket.getByIdentifier", query = "SELECT t FROM Ticket t WHERE identifier = :id")
})
public class Ticket implements Serializable {

    /**
     * request still processing, statusMessage will have updates
     */
    public static final int STATUS_OPEN = 0;
    /**
     * request successfully completed
     */
    public static final int STATUS_FINISHED = 1;
    /**
     * Request could not be completed due to error, see status for details
     */
    public static final int STATUS_ERROR = 2;
    /**
     * Ingest content
     */
    public static final int REQUEST_INGEST = 0;
    /**
     * Retrieve full space
     */
    public static final int REQUEST_FULL_RESTORE = 1;
    /**
     * Retrieve single item
     */
    public static final int REQUEST_SINGLE_ITEM = 2;
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String identifier;
    private int status;
    @Lob
    private String manifest;
    @Lob
    private String statusMessage;
    private String spaceId;
    private String accountId;
    private String itemId;
    private int requestType;

    public String getManifest() {
        return manifest;
    }

    public void setManifest(String manifest) {
        this.manifest = manifest;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }
    
    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Ticket)) {
            return false;
        }
        Ticket other = (Ticket) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ".Ticket[ dbId=" + id + ", ticketId=" + identifier + ", status=" + status + " ]";
    }
}
