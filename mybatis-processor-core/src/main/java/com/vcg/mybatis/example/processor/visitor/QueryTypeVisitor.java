package com.vcg.mybatis.example.processor.visitor;

import com.vcg.mybatis.example.processor.domain.Criterion;
import javax.lang.model.type.*;

public class QueryTypeVisitor implements TypeVisitor<QueryTypeVisitor, Criterion> {

    @Override
    public QueryTypeVisitor visit(TypeMirror t, Criterion c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visit(TypeMirror t) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitPrimitive(PrimitiveType t, Criterion c) {
        c.setJavaType(t.toString());
        return this;
    }

    @Override
    public QueryTypeVisitor visitNull(NullType t, Criterion c) {
        c.setJavaType(t.toString());
        return this;
    }

    @Override
    public QueryTypeVisitor visitArray(ArrayType t, Criterion c) {
        c.setJavaType(t.toString());
        return this;
    }

    @Override
    public QueryTypeVisitor visitDeclared(DeclaredType t, Criterion c) {
        c.setJavaType(t.toString());
        return this;
    }

    @Override
    public QueryTypeVisitor visitError(ErrorType t, Criterion c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitTypeVariable(TypeVariable t, Criterion c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitWildcard(WildcardType t, Criterion c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitExecutable(ExecutableType t, Criterion c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitNoType(NoType t, Criterion c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitUnknown(TypeMirror t, Criterion c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitUnion(UnionType t, Criterion c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitIntersection(IntersectionType t, Criterion c) {
        return this;
    }
}
