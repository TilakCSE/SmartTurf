package interfaces;

import exceptions.InvalidCredentialsException;
import cli.User;

public interface IUserManager {
    void registerUser(String name, String email, String password);
    User loginUser(String email, String password) throws InvalidCredentialsException;
}