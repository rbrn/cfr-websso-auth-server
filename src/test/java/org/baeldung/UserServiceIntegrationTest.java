package org.baeldung;

import org.baeldung.config.ServiceConfig;
import org.baeldung.persistence.dao.RoleRepository;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.dao.VerificationTokenRepository;
import org.baeldung.persistence.dto.UserDto;
import org.baeldung.persistence.model.Privilege;
import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
import org.baeldung.security.error.UserAlreadyExistException;
import org.baeldung.service.IUserService;
import org.baeldung.service.UserService;
import org.baeldung.spring.TestDbConfig;
import org.baeldung.spring.TestIntegrationConfig;
import org.baeldung.validation.EmailExistsException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestDbConfig.class, ServiceConfig.class, TestIntegrationConfig.class })
public class UserServiceIntegrationTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    //

    @Test
    public void givenNewUser_whenRegistered_thenCorrect() throws EmailExistsException {
        final String userEmail = UUID.randomUUID().toString();
        final UserDto userDto = createUserDto(userEmail);

        final User user = userService.registerNewUserAccount(userDto);

        assertNotNull(user);
        assertNotNull(user.getEmail());
        assertEquals(userEmail, user.getEmail());
        assertNotNull(user.getId());
    }

    @Test
    public void givenDetachedUser_whenAccessingEntityAssociations_thenCorrect() throws EmailExistsException {
        final User user = registerUser();
        assertNotNull(user.getRoles());
        user.getRoles().stream().filter(r -> r != null).forEach(Role::getId);
        user.getRoles().stream().filter(r -> r != null).forEach(Role::getName);
        user.getRoles().stream().filter(r -> r != null).forEach(r -> r.getPrivileges().stream().filter(p -> p != null).forEach(Privilege::getId));
        user.getRoles().stream().filter(r -> r != null).forEach(r -> r.getPrivileges().stream().filter(p -> p != null).forEach(Privilege::getName));
        user.getRoles().stream().filter(r -> r != null).forEach(r -> r.getPrivileges().stream().map(Privilege::getRoles).forEach(Assert::assertNotNull));
    }

    @Test
    public void givenDetachedUser_whenServiceLoadById_thenCorrect() throws EmailExistsException {
        final User user = registerUser();
        final User user2 = userService.getUserByID(user.getId());
        assertEquals(user, user2);
    }

    @Test
    public void givenDetachedUser_whenServiceLoadByEmail_thenCorrect() throws EmailExistsException {
        final User user = registerUser();
        final User user2 = userService.findUserByEmail(user.getEmail());
        assertEquals(user, user2);
    }

    @Test(expected = UserAlreadyExistException.class)
    public void givenUserRegistered_whenDuplicatedRegister_thenCorrect() {
        final String email = UUID.randomUUID().toString();
        final UserDto userDto = createUserDto(email);

        userService.registerNewUserAccount(userDto);
        userService.registerNewUserAccount(userDto);
    }

    @Transactional
    public void givenUserRegistered_whenDtoRoleAdmin_thenUserNotAdmin() {
        assertNotNull(roleRepository);
        final UserDto userDto = new UserDto();
        userDto.setEmail(UUID.randomUUID().toString());
        userDto.setPassword("SecretPassword");
        userDto.setMatchingPassword("SecretPassword");
        userDto.setFirstName("First");
        userDto.setLastName("Last");
        assertNotNull(roleRepository.findByName("ROLE_ADMIN"));
        final Long adminRoleId = roleRepository.findByName("ROLE_ADMIN").getId();
        assertNotNull(adminRoleId);
        userDto.setRole(adminRoleId.intValue());
        final User user = userService.registerNewUserAccount(userDto);
        assertFalse(user.getRoles().stream().map(Role::getId).anyMatch(ur -> ur.equals(adminRoleId)));
    }

    @Test
    public void givenUserRegistered_whenCreateToken_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
    }

    @Test
    public void givenUserRegistered_whenCreateTokenCreateDuplicate_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        userService.createVerificationTokenForUser(user, token);
    }

    @Test
    public void givenUserAndToken_whenRemovingUser_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        userService.deleteUser(user);
    }


    @Test
    public void givenTokenValidation_whenValid_thenUserEnabled_andTokenRemoved() {
        User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final long userId = user.getId();
        final String token_status = userService.validateVerificationToken(token);
        assertEquals(token_status, UserService.TOKEN_VALID);
        user = userService.getUserByID(userId);
        assertTrue(user.isEnabled());
    }


    //

    private UserDto createUserDto(final String email) {
        final UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setPassword("SecretPassword");
        userDto.setMatchingPassword("SecretPassword");
        userDto.setFirstName("First");
        userDto.setLastName("Last");
        userDto.setRole(0);
        return userDto;
    }

    private User registerUser() {
        final String email = UUID.randomUUID().toString();
        final UserDto userDto = createUserDto(email);
        final User user = userService.registerNewUserAccount(userDto);
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());
        return user;
    }

}
