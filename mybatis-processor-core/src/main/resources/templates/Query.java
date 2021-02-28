package {{query.packageName}};

import java.util.*;


public class {{query.queryClazzSimpleName}} {

{{#metadata.columnMetadataList}}

	{{#javaDoc}}
	/**
	 * {{{javaDoc}}}
	 */
	{{/javaDoc}}
	private {{javaType}} {{fieldName}};
{{/metadata.columnMetadataList}}

{{#metadata.columnMetadataList}}
	public {{query.queryClazzSimpleName}} get{{firstUpFieldName}}() {
		this.{{fieldName}} = {{fieldName}};
		return this;
	}

	public {{query.queryClazzSimpleName}} set{{firstUpFieldName}}({{javaType}} {{fieldName}}) {
		this.{{fieldName}} = {{fieldName}};
		return this;
	}

{{/metadata.columnMetadataList}}

    public {{query.exampleClazzName}} toExample(){
		{{query.exampleClazzName}} query = {{query.exampleClazzName}}.create();
		{{#query.page}}query.page(q.getPage(),q.getSize());{{/query.page}}
		{{#query.orderBy}}
		if(q.getOrderBy() != null){
        	query.orderBy(q.getOrderBy(),("desc".equalsIgnoreCase(q.getSort())?"desc":"asc"));
        }
		{{/query.orderBy}}
		{{#query.criteria}}
		{{#or}}
		query.or();
		{{/or}}
		{{#criteria}}
        if ({{fieldName}} != null) {
			final {{javaType}} value = {{fieldName}};
			{{#equalTo}}query.and{{firstUpFieldName}}EqualTo(value);{{/equalTo}}{{#notEqualTo}}query.and{{firstUpFieldName}}NotEqualTo(value);{{/notEqualTo}}{{#in}}query.and{{firstUpFieldName}}In(value);{{/in}}{{#notIn}}query.and{{firstUpFieldName}}NotIn(value);{{/notIn}}{{#greaterThan}}query.and{{firstUpFieldName}}GreaterThan(value);{{/greaterThan}}{{#greaterThanOrEqualTo}}query.and{{firstUpFieldName}}GreaterThanOrEqualTo(value);{{/greaterThanOrEqualTo}}{{#lessThan}}query.and{{firstUpFieldName}}LessThan(value);{{/lessThan}}{{#lessThanOrEqualTo}}query.and{{firstUpFieldName}}LessThanOrEqualTo(value);{{/lessThanOrEqualTo}}{{#like}}query.and{{firstUpFieldName}}Like(value);{{/like}}{{#notLike}}query.and{{firstUpFieldName}}NotLike(value);{{/notLike}}
        }
		{{/criteria}}
		{{/query.criteria}}
		return query;
    }


}
