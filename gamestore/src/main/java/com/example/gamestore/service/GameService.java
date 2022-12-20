package com.example.gamestore.service;

import com.example.gamestore.model.dto.GameAddDTO;

public interface GameService {
    void addGame(GameAddDTO gameAddDTO);
    String editGame(String[] tokens);
    String deleteGame(long gameId);
    void allGames();
    void printGameDetails(String gameTitle);
}
