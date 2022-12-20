package com.example.gamestore;

import com.example.gamestore.model.dto.GameAddDTO;
import com.example.gamestore.model.dto.UserLoginDTO;
import com.example.gamestore.model.dto.UserRegisterDTO;
import com.example.gamestore.service.GameService;
import com.example.gamestore.service.OrderService;
import com.example.gamestore.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ConsoleRunner implements CommandLineRunner {
    private static final String USER_REGISTER_COMMAND = "RegisterUser";
    private static final String USER_LOGIN_COMMAND = "LoginUser";
    private static final String USER_LOGOUT_COMMAND = "Logout";
    private static final String ADD_GAME_COMMAND = "AddGame";
    private static final String EDIT_GAME_COMMAND = "EditGame";
    private static final String DELETE_GAME_COMMAND = "DeleteGame";
    private static final String PURCHASE_GAME_COMMAND = "PurchaseGame";
    private static final String ALL_GAMES_COMMAND = "AllGames";
    private static final String DETAIL_GAME_COMMAND = "DetailGame";
    private static final String OWNED_GAMES_COMMAND = "OwnedGames";
    private static final String ADD_ITEM_COMMAND = "AddItem";
    private static final String REMOVE_ITEM_COMMAND = "RemoveItem";
    private static final String BUY_ITEM_COMMAND = "BuyItem";
    private static final String EXIT_COMMAND = "Exit";
    private static final String UNABLE_TO_EXECUTE_MESSAGE = "Unable to execute \"%s\". Command is unknown.\n";
    private static final String GOODBYE_MESSAGE = "GOODBYE! =^.^=";

    private final BufferedReader reader;
    private final UserService userService;
    private final GameService gameService;
    private final OrderService orderService;

    public ConsoleRunner(BufferedReader reader, UserService userService, GameService gameService, OrderService orderService) {
        this.reader = reader;
        this.userService = userService;
        this.gameService = gameService;
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("*****************************************");
        System.out.println("*    Welcome to Game Store DataBase!    *");
        System.out.println("*****************************************");

        while (true) {
            System.out.println("Please enter commands:");
            String[] commands = reader.readLine().split("\\|");

            switch (commands[0]) {
                case USER_REGISTER_COMMAND -> userService.registerUser(new UserRegisterDTO(commands[1], commands[2], commands[3], commands[4]));
                case USER_LOGIN_COMMAND -> userService.loginUser(new UserLoginDTO(commands[1], commands[2]));
                case USER_LOGOUT_COMMAND -> userService.logoutUser();
                case ADD_GAME_COMMAND -> gameService.addGame(new GameAddDTO(commands[1], new BigDecimal(commands[2]), Float.parseFloat(commands[3]), commands[4], commands[5], commands[6], LocalDate.parse(commands[7], DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
                case EDIT_GAME_COMMAND -> System.out.println(gameService.editGame(commands));
                case DELETE_GAME_COMMAND -> System.out.println(gameService.deleteGame(Long.parseLong(commands[1])));
                case PURCHASE_GAME_COMMAND -> userService.purchaseGame(commands[1]);
                case ALL_GAMES_COMMAND -> gameService.allGames();
                case DETAIL_GAME_COMMAND -> gameService.printGameDetails(commands[1]);
                case OWNED_GAMES_COMMAND -> userService.printOwnedGames();
                case ADD_ITEM_COMMAND -> orderService.addItem(commands[1]);
                case REMOVE_ITEM_COMMAND -> orderService.removeItem(commands[1]);
                case BUY_ITEM_COMMAND -> orderService.buyItem();
                case EXIT_COMMAND -> {
                    System.out.println("Exiting...");
                    Thread.sleep(1000);
                    System.out.println(GOODBYE_MESSAGE);
                    return;
                }
                default -> System.out.printf(UNABLE_TO_EXECUTE_MESSAGE, commands[0]);
            }
        }
    }
}
