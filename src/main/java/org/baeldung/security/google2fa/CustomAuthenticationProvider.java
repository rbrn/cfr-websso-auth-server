package org.baeldung.config.google2fa;

import org.baeldung.persistence.model.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

//@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
/*
    @Autowired
    private UserRepository userRepository;*/


    public Authentication someAuthenticate(Authentication authentication)
            throws AuthenticationException {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
                messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.onlySupports",
                        "Only UsernamePasswordAuthenticationToken is supported"));

        // Determine username
        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
                : authentication.getName();

        UserDetails user = null;

        try {
            user = retrieveUser(username,
                    (UsernamePasswordAuthenticationToken) authentication);
        } catch (UsernameNotFoundException notFound) {
            logger.debug("User '" + username + "' not found");

            if (hideUserNotFoundExceptions) {
                throw new BadCredentialsException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.badCredentials",
                        "Bad credentials"));
            } else {
                throw notFound;
            }
        }


        Object principalToReturn = user;

        return createSuccessAuthentication(principalToReturn, authentication, user);
    }


    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        final User user = new User();
        user.setEmail("test@t.com");
        user.setPassword("test");
        user.setEnabled(true);


        if ((user == null)) {
            throw new BadCredentialsException("Invalid username or password");
        }

        final Authentication result = someAuthenticate(auth);
        return new UsernamePasswordAuthenticationToken(user, result.getCredentials(), result.getAuthorities());
    }


    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
