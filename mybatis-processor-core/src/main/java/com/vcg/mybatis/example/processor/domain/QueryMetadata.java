package com.vcg.mybatis.example.processor.domain;

import java.util.List;

public class QueryMetadata {

    private String packageName;

    private String exampleClazzName;

    private String converterClazzSimpleName;

    private String convertClazzName;

    private String queryClazzName;

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

    public String getConverterClazzSimpleName() {
        return converterClazzSimpleName;
    }

    public QueryMetadata setConverterClazzSimpleName(String converterClazzSimpleName) {
        this.converterClazzSimpleName = converterClazzSimpleName;
        return this;
    }

    public String getConvertClazzName() {
        return convertClazzName;
    }

    public QueryMetadata setConvertClazzName(String convertClazzName) {
        this.convertClazzName = convertClazzName;
        if (convertClazzName != null) {
            String[] split = convertClazzName.split("[.]");
            this.converterClazzSimpleName = split[split.length - 1];
        }
        return this;
    }

    public String getQueryClazzName() {
        return queryClazzName;
    }

    public QueryMetadata setQueryClazzName(String queryClazzName) {
        this.queryClazzName = queryClazzName;
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
}
