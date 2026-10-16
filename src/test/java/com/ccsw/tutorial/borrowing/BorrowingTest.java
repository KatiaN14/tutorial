package com.ccsw.tutorial.borrowing;

import com.ccsw.tutorial.borrowing.model.Borrowing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BorrowingTest {
    public static final Long EXISTS_BORROWING_ID = 1L;
    public static final Long NOT_EXISTS_BORROWING_ID = 0L;

    @Mock
    private BorrowingRepository borrowingRepository;

    @InjectMocks
    private BorrowingServiceImpl borrowingService;

    @Test
    public void getExistsBorrowingIdShouldReturnBorrowing() {
        Borrowing borrowing = mock(Borrowing.class);
        when(borrowing.getId()).thenReturn(EXISTS_BORROWING_ID);
        when(borrowingRepository.findById(EXISTS_BORROWING_ID)).thenReturn(Optional.of(borrowing));

        Borrowing borrowingResponse = borrowingService.get(EXISTS_BORROWING_ID);

        assertNotNull(borrowingResponse);

        assertEquals(EXISTS_BORROWING_ID, borrowingResponse.getId());
    }

    @Test
    public void getNotExistsBorrowingIdShouldReturnNull() {
        when(borrowingRepository.findById(NOT_EXISTS_BORROWING_ID)).thenReturn(Optional.empty());

        Borrowing borrowing = borrowingService.get(NOT_EXISTS_BORROWING_ID);

        assertNull(borrowing);
    }
}
