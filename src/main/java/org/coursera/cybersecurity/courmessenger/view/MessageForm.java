package org.coursera.cybersecurity.courmessenger.view;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class MessageForm {

    @NotNull
    @Email
    private String receiverEmail;

    @NotNull
    private String subject;

    @NotNull
    private String body;

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
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

}
