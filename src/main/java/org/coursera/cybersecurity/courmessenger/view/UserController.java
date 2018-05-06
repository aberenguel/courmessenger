package org.coursera.cybersecurity.courmessenger.view;

import java.util.Date;

import javax.validation.Valid;

import org.coursera.cybersecurity.courmessenger.domain.User;
import org.coursera.cybersecurity.courmessenger.repository.UserRepository;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private PasswordValidator passwordValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    @GetMapping("/registration")
    public String registration(Authentication authentication, Model model) {

        // already authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }

        model.addAttribute("userForm", new UserForm());
        return "registration.html";
    }

    @PostMapping("/registration")
    public String userRegistration( //
            @Valid @ModelAttribute UserForm userForm, //
            BindingResult result, //
            Model model //
    ) {

        // user already exists?
        if (repository.existsByEmail(userForm.getEmail())) {
            result.addError(new FieldError("userForm", "email", "Email already registered"));
        }

        if (!result.hasErrors()) {

            // compare password x passwordConfirm
            if (!userForm.passwordsMatch()) {
                result.addError(new ObjectError("userForm", "Passwords mismatch"));

            } else {

                // check if the password is strong enough
                PasswordData passwordData = new PasswordData(userForm.getPassword());
                passwordData.setUsername(userForm.getEmail());
                RuleResult ruleResult = passwordValidator.validate(passwordData);
                if (!ruleResult.isValid()) {

                    // add messages to show in the view
                    for (String message : passwordValidator.getMessages(ruleResult)) {
                        result.addError(new ObjectError("userForm", message));
                    }
                }
            }
        }

        // check form values
        if (result.hasErrors()) {
            return null;
        }

        // build the user
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setEncodedPassword(passwordEncoder.encode(userForm.getPassword()));
        user.setCreationDate(new Date());

        // save the user in database
        repository.save(user);

        model.addAttribute("user", user);

        return "registration-success.html";
    }
}
