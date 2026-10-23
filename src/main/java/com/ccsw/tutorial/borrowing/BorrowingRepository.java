package com.ccsw.tutorial.borrowing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

import com.ccsw.tutorial.borrowing.model.Borrowing;

/**
 * @author KatiaN14
 *
 */
public interface BorrowingRepository extends CrudRepository<Borrowing, Long>, JpaSpecificationExecutor<Borrowing> {

    boolean existsByGameIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIdNot(Long gameId, LocalDate endDate, LocalDate startDate, Long id);
    long countByClientIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIdNot(Long clientId, LocalDate endDate, LocalDate startDate, Long id);

    /**
     * Método para recuperar un listado paginado de {@link Borrowing}
     *
     * @param pageable pageable
     * @return {@link Page} de {@link Borrowing}
     */
    Page<Borrowing> findAll(Pageable pageable);

    /**
     * Método para saber si el cliente con el id tiene algún préstamo
     *
     * @param clientId
     * @return boolean
     */
    boolean existsByClientId(Long clientId);
}
