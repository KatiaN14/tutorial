package com.ccsw.tutorial.borrowing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ccsw.tutorial.borrowing.model.BorrowingDto;
import com.ccsw.tutorial.borrowing.model.BorrowingSearchDto;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.game.model.GameDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BorrowingIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/borrowing";

    public static final Long EXISTING_GAME_ID = 1L; //it is also the game that is borrowed in the time period between start_date and end_date
    public static final Long EXISTING_CLIENT_ID = 1L; //it is also the client that has two borrowings already in the period between start_date and end_date
    public static final LocalDate EXISTING_DATE = LocalDate.of(2026,9,16);
    public static final LocalDate START_DATE = LocalDate.of(2026, 9, 12);
    public static final LocalDate END_DATE = LocalDate.of(2026, 9, 24);
    public static final LocalDate WRONG_END_DATE_1 = LocalDate.of(2025, 9, 10); //end date before start date
    public static final LocalDate WRONG_END_DATE_2 = LocalDate.of(2026, 12, 20); //end date more than 14 days after start date
    public static final Long NOT_EXISTS_GAME = 8L;
    public static final Long NOT_EXISTS_CLIENT = 8L;
    public static final LocalDate NOT_EXISTS_DATE = LocalDate.of(2030,5,10); //date when there are no borrowings
    public static final Long FREE_GAME_ID = 2L;
    public static final Long FREE_CLIENT_ID = 4L;
    public static final Long DELETE_BORROWING_ID = 1L;

    private static final int TOTAL_BORROWINGS = 6;
    private static final int PAGE_SIZE = 5;
    private static final int PAGE_SIZE_10 = 10;

    private static final String GAME_PARAM = "idGame";
    private static final String CLIENT_PARAM = "idClient";
    private static final String DATE_PARAM = "date";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<ResponsePage<BorrowingDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<BorrowingDto>>() {};
    ParameterizedTypeReference<List<BorrowingDto>> responseType = new ParameterizedTypeReference<List<BorrowingDto>>(){};

    private String getUrlWithParams(){
        return UriComponentsBuilder.fromHttpUrl(LOCALHOST + port + SERVICE_PATH)
                .queryParam(GAME_PARAM, "{" + GAME_PARAM + "}")
                .queryParam(CLIENT_PARAM, "{" + CLIENT_PARAM + "}")
                .queryParam(DATE_PARAM, "{" + DATE_PARAM + "}")
                .encode()
                .toUriString();
    }

    @Test
    public void findFirstPageWithFiveSizeShouldReturnFirstFiveResults() {

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_BORROWINGS, response.getBody().getTotalElements());
        assertEquals(PAGE_SIZE, response.getBody().getContent().size());
    }

    @Test
    public void findSecondPageWithFiveSizeShouldReturnLastOneResult() {
        int elementsCount = TOTAL_BORROWINGS - PAGE_SIZE;

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(1,PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_BORROWINGS, response.getBody().getTotalElements());
        assertEquals(elementsCount, response.getBody().getContent().size());
    }

    @Test
    public void saveShouldCreateNewBorrowing() {
        long newBorrowingId = TOTAL_BORROWINGS + 1;
        long newBorrowingSize = TOTAL_BORROWINGS + 1;

        GameDto gameDto = new GameDto();
        gameDto.setId(FREE_GAME_ID);
        ClientDto clientDto = new ClientDto();
        clientDto.setId(FREE_CLIENT_ID);

        BorrowingDto dto = new BorrowingDto();
        dto.setClient(clientDto);
        dto.setGame(gameDto);
        dto.setStartDate(START_DATE);
        dto.setEndDate(END_DATE);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0, (int) newBorrowingSize));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(newBorrowingSize, response.getBody().getTotalElements());

        BorrowingDto borrowing = response.getBody().getContent().stream().filter(item -> item.getId().equals(newBorrowingId)).findFirst().orElse(null);
        assertNotNull(borrowing);
        assertEquals(FREE_GAME_ID, borrowing.getGame().getId());
        assertEquals(FREE_CLIENT_ID, borrowing.getClient().getId());
        assertEquals(START_DATE, borrowing.getStartDate());
        assertEquals(END_DATE, borrowing.getEndDate());
    }

    @Test
    public void deleteWithExistsShouldDeleteBorrowing() {
        long newBorrowingSize = TOTAL_BORROWINGS - 1;

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETE_BORROWING_ID, HttpMethod.DELETE, null, Void.class);

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0 ,TOTAL_BORROWINGS));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(newBorrowingSize, response.getBody().getTotalElements());
    }

    @Test
    public void deleteWithNotExistsShouldThrowException() {
        long deleteBorrowingId = TOTAL_BORROWINGS + 1;

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + deleteBorrowingId, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void findWithoutFiltersShouldReturnAllBorrowings() {
        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0,PAGE_SIZE_10));

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_PARAM, null);
        params.put(CLIENT_PARAM, null);
        params.put(DATE_PARAM, null);

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(TOTAL_BORROWINGS, response.getBody().getContent().size());
    }

    @Test
    public void findExistsGameShouldReturnBorrowings() {
        int BORROWINGS_FILTER = 2; //int elementsCount = TOTAL_BORROWINGS - PAGE_SIZE;

        Map<String, Object> params = new HashMap<>();
        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0,PAGE_SIZE));
        params.put(GAME_PARAM, EXISTING_GAME_ID);
        params.put(CLIENT_PARAM, null);
        params.put(DATE_PARAM, null);

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_FILTER, response.getBody().getContent().size());
    }

    @Test
    public void findExistsClientShouldReturnBorrowings() {
        int BORROWINGS_FILTER = 3;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_PARAM, null);
        params.put(CLIENT_PARAM, EXISTING_CLIENT_ID);
        params.put(DATE_PARAM, null);

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0,PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_FILTER, response.getBody().getContent().size());
    }

    @Test
    public void findExistsDateShouldReturnBorrowings() {
        int BORROWINGS_FILTER = 4;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_PARAM, null);
        params.put(CLIENT_PARAM, null);
        params.put(DATE_PARAM, EXISTING_DATE);

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0,PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_FILTER, response.getBody().getContent().size());
    }

    @Test
    public void findExistsGameAndClientShouldReturnBorrowings() {
        int BORROWINGS_FILTER = 2;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_PARAM, EXISTING_GAME_ID);
        params.put(CLIENT_PARAM, EXISTING_CLIENT_ID);
        params.put(DATE_PARAM, null);

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0,PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_FILTER, response.getBody().getContent().size());
    }

    @Test
    public void findExistsGameAndDateShouldReturnBorrowing() {
        int BORROWINGS_FILTER = 1;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_PARAM, EXISTING_GAME_ID);
        params.put(CLIENT_PARAM, null);
        params.put(DATE_PARAM, EXISTING_DATE);

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0,PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_FILTER, response.getBody().getContent().size());
    }

    @Test
    public void findExistsDateAndClientShouldReturnBorrowings() {
        int BORROWINGS_FILTER = 2;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_PARAM, null);
        params.put(CLIENT_PARAM, EXISTING_CLIENT_ID);
        params.put(DATE_PARAM, EXISTING_DATE);

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0,PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_FILTER, response.getBody().getContent().size());
    }

    @Test
    public void findExistsGameAndClientAndDateShouldReturnBorrowing() {
        int BORROWINGS_FILTER = 1;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_PARAM, EXISTING_GAME_ID);
        params.put(CLIENT_PARAM, EXISTING_CLIENT_ID);
        params.put(DATE_PARAM, EXISTING_DATE);

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0,PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_FILTER, response.getBody().getContent().size());
    }

    @Test
    public void findNotExistsGameShouldReturnEmpty() {
        int BORROWINGS_FILTER = 0;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_PARAM, NOT_EXISTS_GAME);
        params.put(CLIENT_PARAM, null);
        params.put(DATE_PARAM, null);

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0,PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_FILTER, response.getBody().getContent().size());
    }

    @Test
    public void findNotExistsClientShouldReturnEmpty() {
        int BORROWINGS_FILTER = 0;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_PARAM, null);
        params.put(CLIENT_PARAM, NOT_EXISTS_CLIENT);
        params.put(DATE_PARAM, null);

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0,PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_FILTER, response.getBody().getContent().size());
    }

    @Test
    public void findNotExistsDateShouldReturnEmpty() {
        int BORROWINGS_FILTER = 0;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_PARAM, null);
        params.put(CLIENT_PARAM, null);
        params.put(DATE_PARAM, NOT_EXISTS_DATE);

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0,PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_FILTER, response.getBody().getContent().size());
    }

    @Test
    public void saveBorrowingWhenClientReachedLimitShouldThrowException() {
        ClientDto client = new ClientDto();
        client.setId(EXISTING_CLIENT_ID);
        GameDto game = new GameDto();
        game.setId(FREE_GAME_ID);

        BorrowingDto dto = new BorrowingDto();
        dto.setClient(client);
        dto.setGame(game);
        dto.setStartDate(START_DATE);
        dto.setEndDate(END_DATE);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void saveBorrowingWhenGameBorrowedShouldThrowException() {
        ClientDto client = new ClientDto();
        client.setId(FREE_CLIENT_ID);
        GameDto game = new GameDto();
        game.setId(EXISTING_GAME_ID);

        BorrowingDto dto = new BorrowingDto();
        dto.setClient(client);
        dto.setGame(game);
        dto.setStartDate(START_DATE);
        dto.setEndDate(END_DATE);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void saveBorrowingWhenMoreThanFourteenDaysShouldThrowException() {
        ClientDto client = new ClientDto();
        client.setId(FREE_CLIENT_ID);
        GameDto game = new GameDto();
        game.setId(FREE_GAME_ID);

        BorrowingDto dto = new BorrowingDto();
        dto.setClient(client);
        dto.setGame(game);
        dto.setStartDate(START_DATE);
        dto.setEndDate(WRONG_END_DATE_2);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void saveBorrowingWhenEndDateBeforeStartDateShouldThrowException(){
        ClientDto client = new ClientDto();
        client.setId(FREE_CLIENT_ID);
        GameDto game = new GameDto();
        game.setId(FREE_GAME_ID);

        BorrowingDto dto = new BorrowingDto();
        dto.setClient(client);
        dto.setGame(game);
        dto.setStartDate(START_DATE);
        dto.setEndDate(WRONG_END_DATE_1);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
