package com.ccsw.tutorial.borrowing;

import com.ccsw.tutorial.borrowing.model.Borrowing;
import com.ccsw.tutorial.borrowing.model.BorrowingDto;
import com.ccsw.tutorial.borrowing.model.BorrowingSearchDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

/**
 * @author KatiaN14
 *
 */

public interface BorrowingService {
    /**
     * Recupera un {@link Borrowing} a través de su ID
     *
     * @param id PK de la entidad
     * @return {@link Borrowing}
     */
    Borrowing get(Long id);

    /**
     * Método para recuperar un listado paginado y filtrado de {@link Borrowing}
     *
     * @param dto dto de búsqueda
     * @return {@link Page} de {@link Borrowing}
     */
    Page<Borrowing> findPage(BorrowingSearchDto dto, Long idGame, Long idClient, LocalDate date);

    /**
     * Método para crear un {@link Borrowing}
     *
     * @param dto datos de la entidad
     */
    void save(BorrowingDto dto) throws Exception;

    /**
     * Método para eliminar un {@link Borrowing}
     *
     * @param id PK de la entidad
     */
    void delete(Long id) throws Exception;

    /**
     * Recupera un listado de préstamos {@link Borrowing}
     *
     * @return {@link List} de {@link Borrowing}
     */
    List<Borrowing> findAll();
}
