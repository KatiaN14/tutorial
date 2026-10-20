package com.ccsw.tutorial.borrowing;

import com.ccsw.tutorial.borrowing.model.Borrowing;
import com.ccsw.tutorial.borrowing.model.BorrowingDto;
import com.ccsw.tutorial.borrowing.model.BorrowingSearchDto;
import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author KatiaN14
 *
 */
@Service
@Transactional
public class BorrowingServiceImpl implements BorrowingService {

    @Autowired
    BorrowingRepository borrowingRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    GameService gameService;

    @Autowired
    ClientService clientService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Borrowing get(Long id) {

        return this.borrowingRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public Page<Borrowing> findPage(BorrowingSearchDto dto, Long idGame, Long idClient, LocalDate date) {
        BorrowingSpecification gameSpec = new BorrowingSpecification(new SearchCriteria("game.id", ":", idGame));
        BorrowingSpecification clientSpec = new BorrowingSpecification(new SearchCriteria("client.id", ":", idClient));
        BorrowingSpecification startDateSpec = new BorrowingSpecification(new SearchCriteria("startDate", "<=", date));
        BorrowingSpecification endDateSpec = new BorrowingSpecification(new SearchCriteria("endDate", ">=", date));

        Specification<Borrowing> spec = Specification.where(gameSpec).and(clientSpec).and(startDateSpec).and(endDateSpec);
        return this.borrowingRepository.findAll(spec, dto.getPageable().getPageable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, BorrowingDto dto) throws Exception {
        Long gameId = dto.getGame().getId();
        Long clientId = dto.getClient().getId();
        Long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate());
        if (days >= 14) {
            throw new Exception("El préstamo no puede superar los 14 días");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new Exception("La fecha del inicio no puede ser posterior a la de fin");
        }

        if(this.borrowingRepository.existsByGameIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(gameId, dto.getEndDate(), dto.getStartDate())) {
            throw new Exception("El juego ya está prestado en estas fechas");
        }

        if(this.borrowingRepository.countByClientIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(clientId, dto.getEndDate(), dto.getStartDate()) >= 2) {
            throw new Exception ("El cliente ya tiene dos juegos prestados durante las fechas elegidas");
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setClient(clientService.get(dto.getClient().getId()));
        borrowing.setGame(gameService.get(dto.getGame().getId()));
        borrowing.setStartDate(dto.getStartDate());
        borrowing.setEndDate(dto.getEndDate());
        this.borrowingRepository.save(borrowing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {
        if(this.get(id) == null) {
            throw new Exception("Not exists");
        }
        this.borrowingRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Borrowing> findAll() {
        return (List<Borrowing>) this.borrowingRepository.findAll();
    }
}
