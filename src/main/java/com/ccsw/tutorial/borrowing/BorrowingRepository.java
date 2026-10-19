package com.ccsw.tutorial.borrowing;

import com.ccsw.tutorial.borrowing.model.Borrowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

/**
 * @author KatiaN14
 *
 */
public interface BorrowingRepository extends CrudRepository<Borrowing, Long>, JpaSpecificationExecutor<Borrowing> {

    boolean existsByGameIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long gameId, LocalDate endDate, LocalDate startDate);
    long countByClientIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long clientId, LocalDate endDate, LocalDate startDate);

    /**
     * Método para recuperar un listado paginado de {@link Borrowing}
     *
     * @param pageable pageable
     * @return {@link Page} de {@link Borrowing}
     */
    Page<Borrowing> findAll(Pageable pageable);
}
