package org.coursera.cybersecurity.courmessenger.service;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.coursera.cybersecurity.courmessenger.domain.Message;
import org.coursera.cybersecurity.courmessenger.domain.User;
import org.coursera.cybersecurity.courmessenger.repository.MessageRepository;
import org.coursera.cybersecurity.courmessenger.repository.UserRepository;
import org.coursera.cybersecurity.courmessenger.view.MessageForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    
    private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public MessageRepository messageRepository;

    @Autowired
    public UserService userService;

    @Autowired
    public CryptoService cryptoService;

    @Transactional
    public Message buildAndSendMessage(MessageForm messageForm) throws GeneralSecurityException {

        // receiver
        User receiver = userRepository.findByEmail(messageForm.getReceiverEmail());

        // sender (the logged in user)
        User sender = userService.getUserLoggedIn();

        // encrypt the message
        byte[] iv = cryptoService.generateIV();
        byte[] encryptedSubject = cryptoService.encryptData(iv, cryptoService.getSubjectKey(),
                messageForm.getSubject().getBytes());
        byte[] encryptedBody = cryptoService.encryptData(iv, cryptoService.getBodyKey(),
                messageForm.getBody().getBytes());

        // build the object
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setCreationDate(new Date());
        message.setIv(iv);
        message.setEncryptedSubject(encryptedSubject);
        message.setEncryptedBody(encryptedBody);

        // save
        return messageRepository.save(message);
    }

    public void decryptMessage(Message message) throws GeneralSecurityException {

        // decrypt the message
        byte[] iv = message.getIv();
        byte[] decryptedSubject = cryptoService.decryptData(iv, cryptoService.getSubjectKey(),
                message.getEncryptedSubject());
        byte[] decryptedBody = cryptoService.encryptData(iv, cryptoService.getBodyKey(), message.getEncryptedBody());
        
        message.setSubject(new String(decryptedSubject));
        message.setBody(new String(decryptedBody));

    }

    public List<Message> findReceivedMessages() {

        // sender (the logged in user)
        User loggedUser = userService.getUserLoggedIn();

        // fetch messages in database
        List<Message> messages = messageRepository.findByReceiverOrderByCreationDateDesc(loggedUser);

        // decrypt all messages
        for (Message message : messages) {
            try {
                decryptMessage(message);
            } catch (GeneralSecurityException e) {
                LOG.warn("Error decrypting message {}: {}", message.getId(), e.getMessage());
            }
        }

        return messages;
    }
    
    public List<Message> findSentMessages() {

        // sender (the logged in user)
        User loggedUser = userService.getUserLoggedIn();

        // fetch messages in database
        List<Message> messages = messageRepository.findBySenderOrderByCreationDateDesc(loggedUser);

        // decrypt all messages
        for (Message message : messages) {
            try {
                decryptMessage(message);
            } catch (GeneralSecurityException e) {
                LOG.warn("Error decrypting message {}: {}", message.getId(), e.getMessage());
            }
        }

        return messages;
    }

}
