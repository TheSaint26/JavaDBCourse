package com.example.gamestore.service;

import com.example.gamestore.model.dto.UserLoginDTO;
import com.example.gamestore.model.dto.UserRegisterDTO;
import com.example.gamestore.model.entity.User;

public interface UserService {
    void registerUser(UserRegisterDTO userRegisterDTO);
    void loginUser(UserLoginDTO userLoginDTO);

    void logoutUser();
    boolean isUserLogged();
    boolean isUserAdmin();
    void purchaseGame(String gameTitle);
    void printOwnedGames();
    User getLoggedUser();
}
