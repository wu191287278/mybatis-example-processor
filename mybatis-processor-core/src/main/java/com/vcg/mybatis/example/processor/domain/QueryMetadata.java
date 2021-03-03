package com.vcg.mybatis.example.processor.domain;

import java.util.List;

public class QueryMetadata {

    private String packageName;

    private String exampleClazzName;

    private String queryClazzName;

    private String queryClazzSimpleName;

    private List<CriteriaMetadata> criteria;

    private String page;

    private String size;

    private String orderBy;

    private String sort;

    private Integer pageDefault;

    private Integer sizeDefault;

    private String orderByDefault;

    private String sortDefault;

    public String getExampleClazzName() {
        return exampleClazzName;
    }

    public QueryMetadata setExampleClazzName(String exampleClazzName) {
        this.exampleClazzName = exampleClazzName;
        return this;
    }

    public String getQueryClazzName() {
        return queryClazzName;
    }

    public QueryMetadata setQueryClazzName(String queryClazzName) {
        this.queryClazzName = queryClazzName;
        if (queryClazzName != null) {
            String[] split = queryClazzName.split("[.]");
            this.queryClazzSimpleName = split[split.length - 1];
        }
        return this;
    }

    public List<CriteriaMetadata> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<CriteriaMetadata> criteria) {
        this.criteria = criteria;
    }

    public String getPackageName() {
        return packageName;
    }

    public QueryMetadata setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getQueryClazzSimpleName() {
        return queryClazzSimpleName;
    }

    public void setQueryClazzSimpleName(String queryClazzSimpleName) {
        this.queryClazzSimpleName = queryClazzSimpleName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getPageDefault() {
        return pageDefault;
    }

    public void setPageDefault(Integer pageDefault) {
        this.pageDefault = pageDefault;
    }

    public Integer getSizeDefault() {
        return sizeDefault;
    }

    public void setSizeDefault(Integer sizeDefault) {
        this.sizeDefault = sizeDefault;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrderByDefault() {
        return orderByDefault;
    }

    public void setOrderByDefault(String orderByDefault) {
        this.orderByDefault = orderByDefault;
    }

    public String getSortDefault() {
        return sortDefault;
    }

    public void setSortDefault(String sortDefault) {
        this.sortDefault = sortDefault;
    }

}
