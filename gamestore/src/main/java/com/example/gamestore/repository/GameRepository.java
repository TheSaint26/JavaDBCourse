package com.example.gamestore.repository;

import com.example.gamestore.model.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Transactional
    @Modifying
    void deleteGameById(long id);
    Game findGameByTitle(String title);
}
