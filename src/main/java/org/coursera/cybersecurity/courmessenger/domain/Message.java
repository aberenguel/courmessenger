package org.coursera.cybersecurity.courmessenger.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    private User sender;

    @NotNull
    @ManyToOne
    private User receiver;

    @Transient
    private String subject;

    @Transient
    private String body;

    @NotNull
    private byte[] iv;

    @NotNull
    private byte[] encryptedSubject;

    @NotNull
    @Basic(fetch = FetchType.LAZY, optional = false)
    private byte[] encryptedBody;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User destinUser) {
        this.receiver = destinUser;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getEncryptedSubject() {
        return encryptedSubject;
    }

    public void setEncryptedSubject(byte[] encryptedSubject) {
        this.encryptedSubject = encryptedSubject;
    }

    public byte[] getEncryptedBody() {
        return encryptedBody;
    }

    public void setEncryptedBody(byte[] encryptedBody) {
        this.encryptedBody = encryptedBody;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
