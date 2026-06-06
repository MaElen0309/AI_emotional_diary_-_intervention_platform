package com.emotional.diary.common;

import lombok.Data;

@Data
public class PageRequest {
    private int pageNum = 1;
    private int pageSize = 10;
    private String keyword;
    private String sortBy;
    private String sortOrder = "desc";

    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
