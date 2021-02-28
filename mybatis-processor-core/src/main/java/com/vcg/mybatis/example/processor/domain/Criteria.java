package com.vcg.mybatis.example.processor.domain;

import java.util.ArrayList;
import java.util.List;

public class Criteria {

    private List<Criterion> criteria = new ArrayList<>();

    private boolean or;

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<Criterion> criteria) {
        this.criteria = criteria;
    }

    public boolean isOr() {
        return or;
    }

    public void setOr(boolean or) {
        this.or = or;
    }
}
