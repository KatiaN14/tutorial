package com.ccsw.tutorial.game;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

import com.ccsw.tutorial.game.model.Game;

/**
 * @author ccsw
 *
 */
public interface GameRepository extends CrudRepository<Game, Long>, JpaSpecificationExecutor<Game> {
    @Override
    @EntityGraph(attributePaths = {"category", "author"})
    List<Game> findAll(Specification<Game> spec);

    /**
     * Método para saber si existe algún juego asociado a la categoría
     *
     * @param categoryId
     * @return boolean
     */
    boolean existsByCategoryId(Long categoryId);

    /**
     * Método para saber si existe algún juego asociado al autor
     *
     * @param authorId
     * @return boolean
     */
    boolean existsByAuthorId(Long authorId);

}
