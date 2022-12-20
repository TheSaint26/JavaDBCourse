package com.example.gamestore.service.impl;

import com.example.gamestore.model.dto.GameTitleAndPriceDTO;
import com.example.gamestore.model.entity.Game;
import com.example.gamestore.model.entity.User;
import com.example.gamestore.repository.GameRepository;
import com.example.gamestore.repository.UserRepository;
import com.example.gamestore.service.OrderService;
import com.example.gamestore.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private static final String NO_USER_LOGGED_IN_MESSAGE = "No user logged in.";
    private static final String NO_SUCH_GAME = "The game you chose doesn't exist in the database.";
    private static final String GAME_ADDED_TO_CART = "%s added to cart.\n";
    private static final String CART_EMPTY = "Cart is empty.";
    private static final String GAME_IN_CART_MESSAGE = "Game %s is already in the cart.\n";
    private static final String USER_ALREADY_HAS_GAME = "You can't buy %s. You already have it.\n";
    private static final String REMOVED_GAME_MESSAGE = "%s removed from cart.\n";
    private static final String GAME_NOT_IN_CART = "%s is not in the cart.\n";
    private static final String BOUGHT_GAMES_MESSAGE = "Successfully bought games:";
    private final GameRepository gameRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private Set<String> addedGamesTitles;

    public OrderServiceImpl(GameRepository gameRepository, UserService userService, UserRepository userRepository, ModelMapper modelMapper) {
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.addedGamesTitles = new HashSet<>();
    }

    @Override
    public void addItem(String gameTitle) {
        if (userService.isUserLogged()) {
            Game game = gameRepository.findGameByTitle(gameTitle);
            if (game == null) {
                System.out.println(NO_SUCH_GAME);
                return;
            }

            if (this.addedGamesTitles.contains(gameTitle)) {
                System.out.printf(GAME_IN_CART_MESSAGE, gameTitle);
                return;
            }
            Set<String> userOwnedGameTitles = userService.getLoggedUser().getGames()
                            .stream().map(g -> modelMapper.map(g, GameTitleAndPriceDTO.class))
                            .map(GameTitleAndPriceDTO::getTitle)
                                    .collect(Collectors.toSet());
            if (userOwnedGameTitles.contains(gameTitle)) {
                System.out.printf(USER_ALREADY_HAS_GAME, gameTitle);
                return;
            }
            addedGamesTitles.add(gameTitle);
            System.out.printf(GAME_ADDED_TO_CART, game.getTitle());
        } else {
            System.out.println(NO_USER_LOGGED_IN_MESSAGE);
        }
    }

    @Override
    public void removeItem(String gameTitle) {
        if (!userService.isUserLogged()) {
            System.out.println(NO_USER_LOGGED_IN_MESSAGE);
            return;
        }
        if (this.addedGamesTitles.isEmpty()) {
            System.out.println(CART_EMPTY);
            return;
        }
        Game game = gameRepository.findGameByTitle(gameTitle);
        if (game == null) {
            System.out.println(NO_SUCH_GAME);
            return;
        }
        for (String addedGame : addedGamesTitles) {
            if (gameTitle.equals(addedGame)) {
                System.out.printf(REMOVED_GAME_MESSAGE, gameTitle);
                addedGamesTitles.remove(gameTitle);
                return;
            }
        }
        System.out.printf(GAME_NOT_IN_CART, gameTitle);
    }

    @Override
    public void buyItem() {
        if (!userService.isUserLogged()) {
            System.out.println(NO_USER_LOGGED_IN_MESSAGE);
            return;
        }
        if (this.addedGamesTitles.size() == 0) {
            System.out.println(CART_EMPTY);
            return;
        }
        User user = userService.getLoggedUser();

        this.addedGamesTitles
                .stream().map(gameRepository::findGameByTitle)
                .forEach(game -> user.getGames().add(game));
        userRepository.save(user);

        System.out.println(BOUGHT_GAMES_MESSAGE);
        this.addedGamesTitles
                .forEach(title -> System.out.printf(" -%s\n", title));
    }
}
