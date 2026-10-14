package com.ccsw.tutorial.borrowing;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.borrowing.model.BorrowingDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BorrowingIT {
    /*

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/borrowing";

    public static final Long EXISTS_BORROWING_ID = 1L;
    public static final Long NOT_EXISTS_BORROWING_ID = 0L;
    private static final Long NOT_EXISTS_CLIENT = 0L;
    private static final Long EXISTS_CLIENT = 3L;
    private static final Long NOT_EXISTS_GAME = 0L;
    private static final Long EXISTS_GAME = 3L;
    private static final String NOT_EXISTS_TITLE = "NotExists";
    private static final String EXISTS_TITLE_GAME = "Aventureros";
    private static final String EXISTS_NAME_CLIENT = "Cristina";

    private static final String GAME_ID_PARAM = "idGame";
    private static final String CLIENT_ID_PARAM = "idClient";


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<List<BorrowingDto>> responseType = new ParameterizedTypeReference<List<BorrowingDto>>(){};

    private String getUrlWithParams(){
        return null;
    }

    @Test
    public void findWithoutFiltersShouldReturnAllBorrowingsInDB() {

        int BORROWINGS_WITH_FILTER = 3;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_ID_PARAM, null);
        params.put(CLIENT_ID_PARAM, null);

        ResponseEntity<List<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_WITH_FILTER, response.getBody().size());
    }

    @Test
    public void findExistsTitleShouldReturnBorrowings() {

        int BORROWINGS_WITH_FILTER = 1;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_ID_PARAM, EXISTS_TITLE);
        params.put(CLIENT_ID_PARAM, null);

        ResponseEntity<List<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_WITH_FILTER, response.getBody().size());
    }

    @Test
    public void findExistsCategoryShouldReturnBorrowings() {

        int BORROWINGS_WITH_FILTER = 2;

        Map<String, Object> params = new HashMap<>();
        params.put(GAME_ID_PARAM, null);
        params.put(CLIENT_ID_PARAM, EXISTS_CATEGORY);

        ResponseEntity<List<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_WITH_FILTER, response.getBody().size());
    }

    @Test
    public void findExistsTitleAndCategoryShouldReturnBorrowings() {

        int BORROWINGS_WITH_FILTER = 1;

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, EXISTS_CATEGORY);

        ResponseEntity<List<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_WITH_FILTER, response.getBody().size());
    }

    @Test
    public void findNotExistsTitleShouldReturnEmpty() {

        int BORROWINGS_WITH_FILTER = 0;

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, NOT_EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_WITH_FILTER, response.getBody().size());
    }

    @Test
    public void findNotExistsCategoryShouldReturnEmpty() {

        int BORROWINGS_WITH_FILTER = 0;

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, null);
        params.put(CATEGORY_ID_PARAM, NOT_EXISTS_CATEGORY);

        ResponseEntity<List<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(BORROWINGS_WITH_FILTER, response.getBody().size());
    }

    @Test
    public void findNotExistsTitleOrCategoryShouldReturnEmpty() {

        int BORROWINGS_WITH_FILTER = 0;

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, NOT_EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, NOT_EXISTS_CATEGORY);

        ResponseEntity<List<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);
        assertNotNull(response);
        assertEquals(BORROWINGS_WITH_FILTER, response.getBody().size());

        params.put(TITLE_PARAM, NOT_EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, EXISTS_CATEGORY);

        response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);
        assertNotNull(response);
        assertEquals(BORROWINGS_WITH_FILTER, response.getBody().size());

        params.put(TITLE_PARAM, EXISTS_TITLE);
        params.put(CATEGORY_ID_PARAM, NOT_EXISTS_CATEGORY);

        response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);
        assertNotNull(response);
        assertEquals(BORROWINGS_WITH_FILTER, response.getBody().size());
    }

    @Test
    public void saveWithoutIdShouldCreateNewBorrowing() {

        BorrowingDto dto = new BorrowingDto();
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1L);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);

        dto.setTitle(NEW_TITLE);
        dto.setAge("18");
        dto.setAuthor(authorDto);
        dto.setCategory(categoryDto);

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, NEW_TITLE);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(0, response.getBody().size());

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void modifyWithExistIdShouldModifyBorrowing() {

        BorrowingDto dto = new BorrowingDto();
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1L);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);

        dto.setTitle(NEW_TITLE);
        dto.setAge("18");
        dto.setAuthor(authorDto);
        dto.setCategory(categoryDto);

        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_PARAM, NEW_TITLE);
        params.put(CATEGORY_ID_PARAM, null);

        ResponseEntity<List<BorrowingDto>> response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(0, response.getBody().size());

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + EXISTS_BORROWING_ID, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        response = restTemplate.exchange(getUrlWithParams(), HttpMethod.GET, null, responseType, params);

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
        assertEquals(EXISTS_BORROWING_ID, response.getBody().get(0).getId());
    }

    @Test
    public void modifyWithNotExistIdShouldThrowException() {

        BorrowingDto dto = new BorrowingDto();
        dto.setTitle(NEW_TITLE);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NOT_EXISTS_BORROWING_ID, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

     */

}
