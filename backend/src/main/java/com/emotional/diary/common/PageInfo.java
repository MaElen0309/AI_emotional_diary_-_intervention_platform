package com.emotional.diary.common;

import lombok.Data;
import java.util.List;

@Data
public class PageInfo<T> {
    private List<T> list;
    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;

    public PageInfo(List<T> list, long total, PageRequest pageRequest) {
        this.list = list;
        this.total = total;
        this.pageNum = pageRequest.getPageNum();
        this.pageSize = pageRequest.getPageSize();
        this.pages = (int) Math.ceil((double) total / pageRequest.getPageSize());
    }
}
