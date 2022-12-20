package com.example.gamestore.service.impl;

import com.example.gamestore.model.dto.UserLoginDTO;
import com.example.gamestore.model.dto.UserRegisterDTO;
import com.example.gamestore.model.entity.Game;
import com.example.gamestore.model.entity.User;
import com.example.gamestore.repository.GameRepository;
import com.example.gamestore.repository.UserRepository;
import com.example.gamestore.service.UserService;
import com.example.gamestore.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private static final String PASSWORDS_NOT_MATCH = "Passwords don't match!";
    private static final String NO_GAME_WITH_NAME = "There's no game with name %s in the database.\n";
    private static final String USED_EMAIL_MESSAGE = "User with email: %s already exists!\n";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username / password!";
    private static final String SUCCESSFULLY_REGISTERED_USER_MESSAGE = "%s was registered.\n";
    private static final String SUCCESSFULLY_LOGGED_MESSAGE = "Successfully logged in %s\n";
    private static final String CANNOT_LOG_OUT_MESSAGE = "Cannot log out. No user was logged in.";
    private static final String SUCCESSFUL_LOGOUT_MESSAGE = "User %s successfully logged out.\n";
    private static final String NO_USER_LOGGED_IN_MESSAGE = "No user logged in.";
    private static final String USER_HAS_NO_GAMES_MESSAGE = "User: %s doesn't have games yet.\n";
    private User loggedUser;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public UserServiceImpl(ModelMapper modelMapper, ValidationUtil validationUtil, UserRepository userRepository, GameRepository gameRepository) {
        this.loggedUser = null;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public void registerUser(UserRegisterDTO userRegisterDTO) {
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            System.out.println(PASSWORDS_NOT_MATCH);
            return;
        }
        Set<ConstraintViolation<UserRegisterDTO>> violations = validationUtil
                .getViolations(userRegisterDTO);
        if (!violations.isEmpty()) {
            violations
                    .forEach(v -> System.out.println(v.getMessage()));
            return;
        }
        User anotherOne = userRepository.findUserByEmail(userRegisterDTO.getEmail());
        if (anotherOne != null) {
            System.out.printf(USED_EMAIL_MESSAGE, userRegisterDTO.getEmail());
            return;
        }
        User user = modelMapper.map(userRegisterDTO, User.class);
        if (userRepository.count() == 0) {
            user.setAdmin(true);
        }
        userRepository.save(user);
        System.out.printf(SUCCESSFULLY_REGISTERED_USER_MESSAGE, user.getFullName());
    }

    @Override
    public void loginUser(UserLoginDTO userLoginDTO) {
        Set<ConstraintViolation<UserLoginDTO>> violations = validationUtil.getViolations(userLoginDTO);
        if (!violations.isEmpty()) {
            violations
                    .forEach(v -> System.out.println(v.getMessage()));
            return;
        }
        User user = userRepository.findUserByEmailAndPassword(userLoginDTO.getEmail(), userLoginDTO.getPassword());
        if (user == null) {
            System.out.println(INVALID_CREDENTIALS_MESSAGE);
            return;
        }
        loggedUser = user;
        System.out.printf(SUCCESSFULLY_LOGGED_MESSAGE, user.getFullName());
    }

    @Override
    public void logoutUser() {
        if (loggedUser == null) {
            System.out.println(CANNOT_LOG_OUT_MESSAGE);
            return;
        }
        String fullName = loggedUser.getFullName();
        loggedUser = null;
        System.out.printf(SUCCESSFUL_LOGOUT_MESSAGE, fullName);
    }

    @Override
    public boolean isUserLogged() {
        return loggedUser != null;
    }

    @Override
    public boolean isUserAdmin() {
        return loggedUser.isAdmin();
    }

    @Override
    @Transactional
    public void purchaseGame(String gameTitle) {
        if (isUserLogged()) {
            Game game = gameRepository.findGameByTitle(gameTitle);
            if (game == null) {
                System.out.printf(NO_GAME_WITH_NAME, gameTitle);
                return;
            }
            loggedUser.getGames().add(game);
            userRepository.save(loggedUser);
        }
    }

    @Override
    public void printOwnedGames() {
        if (loggedUser == null) {
            System.out.println(NO_USER_LOGGED_IN_MESSAGE);
            return;
        }
        if (loggedUser.getGames().isEmpty()) {
            System.out.printf(USER_HAS_NO_GAMES_MESSAGE, loggedUser.getFullName());
            return;
        }
        loggedUser.getGames()
                .stream()
                .map(Game::getTitle)
                .forEach(System.out::println);
    }

    @Override
    public User getLoggedUser() {
        return loggedUser;
    }
}

