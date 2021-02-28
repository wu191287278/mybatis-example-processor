package com.vcg.mybatis.example.processor.domain;

import java.util.List;

public class QueryMetadata {

    private String packageName;

    private String exampleClazzName;

    private String queryClazzName;

    private String queryClazzSimpleName;

    private List<Criteria> criteria;

    private Boolean page;

    private Boolean orderBy;

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

    public List<Criteria> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<Criteria> criteria) {
        this.criteria = criteria;
    }

    public String getPackageName() {
        return packageName;
    }

    public QueryMetadata setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public Boolean getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Boolean orderBy) {
        this.orderBy = orderBy;
    }

    public Boolean getPage() {
        return page;
    }

    public void setPage(Boolean page) {
        this.page = page;
    }

    public String getQueryClazzSimpleName() {
        return queryClazzSimpleName;
    }

    public void setQueryClazzSimpleName(String queryClazzSimpleName) {
        this.queryClazzSimpleName = queryClazzSimpleName;
    }
}
