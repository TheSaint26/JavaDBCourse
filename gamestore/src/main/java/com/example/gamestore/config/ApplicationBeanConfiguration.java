package com.example.gamestore.config;

import com.example.gamestore.model.dto.GameAddDTO;
import com.example.gamestore.model.entity.Game;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
public class ApplicationBeanConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(GameAddDTO.class, Game.class)
                .addMappings(mapper ->
                        mapper.map(GameAddDTO::getThumbnailURL,
                                Game::setImageThumbnail));
        return modelMapper;
    }
    @Bean
    public BufferedReader reader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }
}
