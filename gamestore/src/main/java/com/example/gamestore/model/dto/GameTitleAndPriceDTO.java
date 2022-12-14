package com.example.gamestore.model.dto;

import java.math.BigDecimal;

public class GameTitleAndPriceDTO {
    private String title;
    private BigDecimal price;

    public GameTitleAndPriceDTO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("%s %s", getTitle(), getPrice());
    }
}
