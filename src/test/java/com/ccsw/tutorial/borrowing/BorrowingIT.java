package com.ccsw.tutorial.borrowing;

import com.ccsw.tutorial.borrowing.model.BorrowingDto;
import com.ccsw.tutorial.borrowing.model.BorrowingSearchDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BorrowingIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/borrowing";

    public static final Long DELETE_BORROWING_ID = 2L;
    public static final String EXISTING_GAME = "Eurogames";
    public static final String EXISTING_CLIENT = "Pablo";
    public static final LocalDate START_DATE = LocalDate.of(2026, 9, 12);
    public static final LocalDate END_DATE = LocalDate.of(2026, 9, 24);

    private static final int TOTAL_BORROWINGS = 5;
    private static final int PAGE_SIZE = 3;

    ParameterizedTypeReference<List<BorrowingDto>> responseTypeList = new ParameterizedTypeReference<List<BorrowingDto>>(){};

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<ResponsePage<BorrowingDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<BorrowingDto>>() {};

    @Test
    public void findFirstPageWithThreeSizeShouldReturnFirstThreeResults() {

        BorrowingSearchDto searchDto = new BorrowingSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<BorrowingDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_BORROWINGS, response.getBody().getTotalElements());
        assertEquals(PAGE_SIZE, response.getBody().getContent().size());
    }

    @Test
    public void findSecondPageWithThreeSizeShouldReturnLastTwoResults() {
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

        BorrowingDto dto = new BorrowingDto();
        dto.setGame(EXISTING_GAME);
        dto.setClient(EXISTING_CLIENT);
    }

}
