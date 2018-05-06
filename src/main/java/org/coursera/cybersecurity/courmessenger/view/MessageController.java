package org.coursera.cybersecurity.courmessenger.view;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.validation.Valid;

import org.coursera.cybersecurity.courmessenger.domain.Message;
import org.coursera.cybersecurity.courmessenger.repository.UserRepository;
import org.coursera.cybersecurity.courmessenger.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MessageController {

    @Autowired
    public MessageService messageService;

    @Autowired
    public UserRepository userRepository;

    @GetMapping("/messages/inbox")
    public String viewReceivedMessages(Model model) {

        List<Message> messages = messageService.findReceivedMessages();

        model.addAttribute("messages", messages);

        return "messages-inbox";
    }
    
    @GetMapping("/messages/outbox")
    public String viewSendMessages(Model model) {

        List<Message> messages = messageService.findSentMessages();

        model.addAttribute("messages", messages);

        return "messages-outbox";
    }

    @GetMapping("/messages/new")
    public String composeMessage(Model model) {

        model.addAttribute("messageForm", new MessageForm());

        return "messages-new";
    }

    @PostMapping("/messages")
    public String sendMessage( //
            @Valid @ModelAttribute MessageForm messageForm, //
            BindingResult result, //
            Model model //
    ) throws GeneralSecurityException {

        if (result.hasErrors()) {
            return "messages-new";
        }

        // check if receiver exists
        if (!userRepository.existsByEmail(messageForm.getReceiverEmail())) {
            result.addError(new FieldError("userForm", "receiverEmail", "Sender does not exist"));
        }

        if (result.hasErrors()) {
            return "messages-new";
        }

        // build the message
        messageService.buildAndSendMessage(messageForm);

        return "redirect:/messages/inbox";
    }
}
