package {{metadata.packageName}};

import java.util.*;


public class {{metadata.converterClazzSimpleName}} {

	private {{metadata.converterClazzSimpleName}}() {

	}

    public static {{metadata.exampleClazzName}} apply({{metadata.queryClazzName}} q){
		{{metadata.exampleClazzName}} query = {{metadata.exampleClazzName}}.create();
		{{#metadata.page}}query.page(q.getPage(),q.getSize());{{/metadata.page}}
		{{#metadata.orderBy}}
		if(q.getOrderBy() != null){
        	query.orderBy(q.getOrderBy(),("desc".equalsIgnoreCase(q.getSort())?"desc":"asc"));
        }
		{{/metadata.orderBy}}
		{{#metadata.criteria}}
		{{#or}}
		query.or();
		{{/or}}
		{{#criteria}}
        if (q.get{{firstUpFieldAliasName}}() != null) {
			{{javaType}} value = q.get{{firstUpFieldAliasName}}();
			{{#equalTo}}query.and{{firstUpFieldName}}EqualTo(value);{{/equalTo}}{{#notEqualTo}}query.and{{firstUpFieldName}}NotEqualTo(value);{{/notEqualTo}}{{#in}}query.and{{firstUpFieldName}}In(value);{{/in}}{{#notIn}}query.and{{firstUpFieldName}}NotIn(value);{{/notIn}}{{#greaterThan}}query.and{{firstUpFieldName}}GreaterThan(value);{{/greaterThan}}{{#greaterThanOrEqualTo}}query.and{{firstUpFieldName}}GreaterThanOrEqualTo(value);{{/greaterThanOrEqualTo}}{{#lessThan}}query.and{{firstUpFieldName}}LessThan(value);{{/lessThan}}{{#lessThanOrEqualTo}}query.and{{firstUpFieldName}}LessThanOrEqualTo(value);{{/lessThanOrEqualTo}}{{#like}}query.and{{firstUpFieldName}}Like(value);{{/like}}{{#notLike}}query.and{{firstUpFieldName}}NotLike(value);{{/notLike}}
        }
		{{/criteria}}
		{{/metadata.criteria}}
		return query;
    }


}
