package org.baeldung.config.service;

import org.baeldung.config.dto.UserDto;
import org.baeldung.config.error.UserAlreadyExistException;
import org.baeldung.persistence.model.User;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface IUserService {

    User registerNewUserAccount(UserDto accountDto) throws UserAlreadyExistException;

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void deleteUser(User user);

    void createVerificationTokenForUser(User user, String token);


    void createPasswordResetTokenForUser(User user, String token);

    User findUserByEmail(String email);


    User getUserByPasswordResetToken(String token);

    User getUserByID(long id);

    void changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String password);

    String validateVerificationToken(String token);

    String generateQRUrl(User user) throws UnsupportedEncodingException;

    User updateUser2FA(boolean use2FA);

    List<String> getUsersFromSessionRegistry();

}
