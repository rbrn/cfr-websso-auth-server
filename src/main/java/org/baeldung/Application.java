package org.baeldung;

import org.baeldung.persistence.dto.UserDto;
import org.baeldung.web.RegistrationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {

        ConfigurableApplicationContext app = SpringApplication.run(Application.class, args);

        RegistrationController myBean = (RegistrationController)app.getBean(RegistrationController.class);
        UserDto user = new UserDto();
        user.setEmail("costin" + "@example.com");
        user.setPassword("123");
        user.setFirstName("Costin");
        user.setLastName("Aldea");
        user.setMatchingPassword("123");

        myBean.registerUserAccount(user);
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

}