package com.example.gamestore.service;

public interface OrderService {
    void addItem(String gameTitle);
    void removeItem(String gameTitle);
    void buyItem();
}
