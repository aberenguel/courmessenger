package org.coursera.cybersecurity.courmessenger;

import java.io.InputStreamReader;

import org.passay.CharacterRule;
import org.passay.DictionarySubstringRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordValidator;
import org.passay.dictionary.Dictionary;
import org.passay.dictionary.DictionaryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final int MIN_PASSWORD_LEN = 8;

    @Autowired
    private UserService userService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/stylesheets/**", "/webjars/**", "/favicon.ico", "/error", "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http 
            .authorizeRequests()
                .antMatchers("/", "/registration").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home")
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll();
      //@formatter:on
    }

    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Bean
    public PasswordValidator passwordValidator() {

        Dictionary dictionary = new DictionaryBuilder() //
                .addReader(new InputStreamReader(getClass().getResourceAsStream("/passwords/john8chars.txt"))) // John the Ripper
                .addReader(new InputStreamReader(getClass().getResourceAsStream("/passwords/cain8chars.txt"))) // Cain & Abel
                .build();

        // format:off
        return new PasswordValidator(
                // at least 8 characters
                new LengthRule(MIN_PASSWORD_LEN, Integer.MAX_VALUE),

                // at least 5 alphabetical character
                new CharacterRule(EnglishCharacterData.Alphabetical, 5),

                // at least one digit character (disabled)
                // new CharacterRule(EnglishCharacterData.Digit, 1),

                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),

                // not allow password in the dictionary
                new DictionarySubstringRule(dictionary));
        // format:on
    }
}