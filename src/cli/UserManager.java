package cli;

import interfaces.IUserManager;
import exceptions.InvalidCredentialsException;
import java.util.ArrayList;
import java.util.List;

public class UserManager implements IUserManager {
    private List<User> users;

    public UserManager() {
        users = new ArrayList<>();
    }

    @Override
    public void registerUser(String name, String email, String password) {
        users.add(new User(name, email, password));
    }

    @Override
    public User loginUser(String email, String password) throws InvalidCredentialsException {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new InvalidCredentialsException("Invalid email or password!");
    }
}