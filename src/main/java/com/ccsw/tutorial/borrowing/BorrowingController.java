package com.ccsw.tutorial.borrowing;

import com.ccsw.tutorial.borrowing.model.Borrowing;
import com.ccsw.tutorial.borrowing.model.BorrowingDto;
import com.ccsw.tutorial.borrowing.model.BorrowingSearchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author KatiaN14
 *
 */

@Tag(name = "Borrowing", description = "API of Borrowing")
@RequestMapping(value = "/borrowing")
@RestController
@CrossOrigin(origins = "*")
public class BorrowingController {

    @Autowired
    BorrowingService borrowingService;

    @Autowired
    ModelMapper mapper;

    /**
     * Método para recuperar un listado paginado de {@link Borrowing}
     *
     * @param dto dto de búsqueda
     * @param idGame PK del juego
     * @param idClient PK del cliente
     * @param date fecha elegida en la búsqueda
     * @return {@link Page} de {@link BorrowingDto}
     */
    @Operation(summary = "Find Page", description = "Method that returns a page of filtered Borrowings")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public Page<BorrowingDto> findPage(
            @RequestParam(value = "idGame", required = false) Long idGame,
            @RequestParam(value = "idClient", required = false) Long idClient,
            @RequestParam(value = "date", required = false) LocalDate date,
            @RequestBody BorrowingSearchDto dto) {
        Page<Borrowing> page = this.borrowingService.findPage(dto, idGame, idClient, date);
        return new PageImpl<>(page.getContent().stream().map(e -> mapper.map(e, BorrowingDto.class)).collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

    /**
     * Método para crear un {@link Borrowing}
     *
     * @param dto datos de la entidad
     */
    @Operation(summary = "Save", description = "Method that saves a Borrowing")
    @RequestMapping(path = {""}, method = RequestMethod.PUT)
    public void save(@RequestBody BorrowingDto dto) throws Exception {
        this.borrowingService.save(dto);
    }

    /**
     * Método para borrar un {@link Borrowing}
     *
     * @param id PK de la entidad
     */
    @Operation(summary = "Delete", description = "Method that deletes a Borrowing")
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id) throws Exception {
        this.borrowingService.delete(id);
    }

    /**
     * Recupera un listado de préstamos {@link Borrowing}
     *
     * @return {@link List} de {@link BorrowingDto}
     */
    @Operation(summary = "Find", description = "Method that returns a list of Borrowings")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<BorrowingDto> findAll() {
        List<Borrowing> borrowings = this.borrowingService.findAll();
        return borrowings.stream().map(e -> mapper.map(e, BorrowingDto.class)).collect(Collectors.toList());
    }
}
