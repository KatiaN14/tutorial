package com.ccsw.tutorial.borrowing.model;

import com.ccsw.tutorial.common.pagination.PageableRequest;

/**
 * @author KatiaN14
 *
 */

public class BorrowingSearchDto {
    private PageableRequest pageable;

    public PageableRequest getPageable() { return pageable; }

    public void setPageable(PageableRequest pageable) {
        this.pageable = pageable;
    }
}
