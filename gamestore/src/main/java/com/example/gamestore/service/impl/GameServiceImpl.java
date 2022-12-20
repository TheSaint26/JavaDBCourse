package com.example.gamestore.service.impl;

import com.example.gamestore.model.dto.GameAddDTO;
import com.example.gamestore.model.dto.GameDetailsDTO;
import com.example.gamestore.model.dto.GameTitleAndPriceDTO;
import com.example.gamestore.model.entity.Game;
import com.example.gamestore.repository.GameRepository;
import com.example.gamestore.service.GameService;
import com.example.gamestore.service.UserService;
import com.example.gamestore.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class GameServiceImpl implements GameService {
    private static final String SET_PRICE_METHOD_NAME = "setPrice";
    private static final String SET_SIZE_METHOD_NAME = "setSize";
    private static final String SET_DESCRIPTION_METHOD_NAME = "setDescription";
    private static final String SET_IMAGE_THUMBNAIL_METHOD_NAME = "setImageThumbnail";
    private static final String SET_TRAILER_METHOD_NAME = "setTrailer";
    private static final String SET_RELEASE_DATE = "setReleaseDate";
    private static final String NOT_ALLOWED_ACTION = "Only logged in admins are allowed to %s games\n";
    private static final String GAME_NOT_EXISTS_MESSAGE = "Game ID: %d doesn't exist in the database!\n";
    private static final String ADDED_GAME_MESSAGE = "Added %s.\n";
    private static final String EDITED_GAME_MESSAGE = "Edited %s\n";
    private static final String DELETED_GAME_MESSAGE = "Deleted %s\n";
    private static final String EMPTY_DATABASE_MESSAGE = "Database is empty";
    private static final String GAME_NAME_NOT_EXISTS_MESSAGE = "Game %s doesn't exist in the database!\n";
    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final UserService userService;

    public GameServiceImpl(GameRepository gameRepository, ModelMapper modelMapper, ValidationUtil validationUtil, UserService userService) {
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.userService = userService;
    }

    @Override
    public void addGame(GameAddDTO gameAddDTO) {
        if (userService.isUserLogged() && userService.isUserAdmin()) {
            Set<ConstraintViolation<GameAddDTO>> violations = validationUtil.getViolations(gameAddDTO);
            if (!violations.isEmpty()) {
                violations
                        .forEach(v -> System.out.println(v.getMessage()));
                return;
            }
            Game game = modelMapper.map(gameAddDTO, Game.class);
            gameRepository.save(game);
            System.out.printf(ADDED_GAME_MESSAGE, game.getTitle());
        } else {
            System.out.printf(NOT_ALLOWED_ACTION, "add");
        }
    }

    /**
     *
     * @param tokens an array of strings from the console.
     *               First element is skipped on purpose,
     *               because it is a command used in ConsoleRunner class.
     * @return edited game title as string.
     */
    @Override
    public String editGame(String[] tokens) {
        if (userService.isUserLogged() && userService.isUserAdmin()) {
            long gameId = Long.parseLong(tokens[1]);
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return String.format(GAME_NOT_EXISTS_MESSAGE, gameId);
            }
            Class<Game> clazz = Game.class;
            Method[] methods = clazz.getDeclaredMethods();
            Arrays.stream(tokens)
                    .skip(2)
                    .forEach(t -> {
                        String fieldName = t.split("=")[0];
                        String finalFieldName = String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1);
                        String value = t.split("=")[1];
                        Method method = Arrays.stream(methods).filter(m -> m.getName().endsWith(finalFieldName) && m.getName().startsWith("set")).findFirst().orElse(null);
                        switch (method.getName()) {
                            case SET_PRICE_METHOD_NAME -> game.setPrice(BigDecimal.valueOf(Double.parseDouble(value)));
                            case SET_SIZE_METHOD_NAME -> {
                                float newSize = Float.parseFloat(value);
                                game.setSize(newSize);
                            }
                            case SET_DESCRIPTION_METHOD_NAME -> game.setDescription(value);
                            case SET_IMAGE_THUMBNAIL_METHOD_NAME -> game.setImageThumbnail(value);
                            case SET_TRAILER_METHOD_NAME -> game.setTrailer(value);
                            case SET_RELEASE_DATE -> game.setReleaseDate(LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        }
                    });

            gameRepository.save(game);
            return String.format(EDITED_GAME_MESSAGE, game.getTitle());
        } else {
            return String.format(NOT_ALLOWED_ACTION, "edit");
        }
    }

    @Override
    public String deleteGame(long gameId) {
        if (userService.isUserLogged() && userService.isUserAdmin()) {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return String.format(GAME_NOT_EXISTS_MESSAGE, gameId);
            }
            gameRepository.deleteGameById(gameId);

            return String.format(DELETED_GAME_MESSAGE, game.getTitle());
        } else {
            return String.format(NOT_ALLOWED_ACTION, "delete");
        }
    }

    @Override
    public void allGames() {
        List<Game> games = gameRepository.findAll();
        if (games.isEmpty()) {
            System.out.println(EMPTY_DATABASE_MESSAGE);
            return;
        }
        games.stream()
                .map(game -> modelMapper.map(game, GameTitleAndPriceDTO.class))
                .forEach(g -> System.out.println(g.toString()));
    }

    @Override
    public void printGameDetails(String gameTitle) {
        Game game = gameRepository.findGameByTitle(gameTitle);
        if (game == null) {
            System.out.printf(GAME_NAME_NOT_EXISTS_MESSAGE, gameTitle);
            return;
        }
        GameDetailsDTO gameDetailsDTO = modelMapper.map(game, GameDetailsDTO.class);
        System.out.println(gameDetailsDTO.toString());
    }
}
