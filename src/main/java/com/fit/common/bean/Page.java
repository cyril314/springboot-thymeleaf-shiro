package com.fit.common.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @AUTO 分页基类
 * @Author Cyril
 * @DATE 2023/5/8
 */
public class Page<T> {

    public static final int DEFAULT_PAGE_SIZE = 10;

    protected int pageNo = 1; // 当前页, 默认为第1页
    protected int pageSize = DEFAULT_PAGE_SIZE; // 每页记录数
    protected long totalRecord = -1; // 总记录数, 默认为-1, 表示需要查询
    protected int totalPage = -1; // 总页数, 默认为-1, 表示需要计算

    protected List<T> results = new ArrayList<T>(); // 当前页记录List形式

    public Page(List<T> list) {
        this.results = list;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        computeTotalPage();
    }

    public long getTotalRecord() {
        return totalRecord;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setTotalRecord(long totalRecord) {
        this.totalRecord = totalRecord;
        computeTotalPage();
    }

    protected void computeTotalPage() {
        if (getPageSize() > 0 && getTotalRecord() > -1) {
            this.totalPage = (int) (getTotalRecord() % getPageSize() == 0 ? getTotalRecord() / getPageSize() : getTotalRecord() / getPageSize() + 1);
        }
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("Page [pageNo=").append(pageNo).append(", pageSize=").append(pageSize)
                .append(", totalRecord=").append(totalRecord < 0 ? "null" : totalRecord).append(", totalPage=")
                .append(totalPage < 0 ? "null" : totalPage).append(", curPageObjects=").append(results == null ? "null" : results.size()).append("]");
        return builder.toString();
    }
}
