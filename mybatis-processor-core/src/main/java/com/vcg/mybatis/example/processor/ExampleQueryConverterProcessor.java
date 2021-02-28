package com.vcg.mybatis.example.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.vcg.mybatis.example.processor.converter.annotation.*;
import com.vcg.mybatis.example.processor.domain.Criteria;
import com.vcg.mybatis.example.processor.domain.Criterion;
import com.vcg.mybatis.example.processor.domain.QueryMetadata;
import com.vcg.mybatis.example.processor.visitor.QueryTypeVisitor;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.persistence.Transient;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.vcg.mybatis.example.processor.ExampleQueryConverter")
public class ExampleQueryConverterProcessor extends AbstractProcessor {

    private final QueryTypeVisitor visitor = new QueryTypeVisitor();


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Filer filer = processingEnv.getFiler();
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ExampleQueryConverter.class);
            MustacheFactory mf = new DefaultMustacheFactory() {
                @Override
                public void encode(String value, Writer writer) {
                    try {
                        writer.write(value);
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            };
            ClassLoader classLoader = ExampleQueryConverterProcessor.class.getClassLoader();

            for (Element element : elements) {
                QueryMetadata metadata = read(element);
                String convertClazzName = metadata.getConvertClazzName();

                JavaFileObject javaFileObject = filer.createSourceFile(convertClazzName);
                HashMap<String, Object> scopes = new HashMap<>();
                scopes.put("metadata", metadata);
                ExampleQueryConverter query = element.getAnnotation(ExampleQueryConverter.class);
                InputStream exampleInputStream = classLoader.getResourceAsStream(query.template());
                try (InputStreamReader in = new InputStreamReader(exampleInputStream, StandardCharsets.UTF_8);
                     Writer writer = javaFileObject.openWriter()) {
                    Mustache mustache = mf.compile(in, convertClazzName);
                    mustache.execute(writer, scopes);
                }
            }

        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, Arrays.toString(e.getStackTrace()));
        }
        return true;
    }


    public QueryMetadata read(Element element) {
        ExampleQueryConverter query = element.getAnnotation(ExampleQueryConverter.class);
        PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(element);
        String packageName = packageOf.toString();
        String clazzName = element.toString();

        String convertName = (clazzName + "Converter");
        String example = query.example();
        QueryMetadata metadata = new QueryMetadata()
                .setQueryClazzName(clazzName)
                .setConvertClazzName(convertName)
                .setExampleClazzName(example)
                .setPackageName(packageName);

        Criteria criteria = new Criteria();
        List<Criteria> list = new ArrayList<>();
        List<Criteria> orList = new ArrayList<>();
        boolean page = false;
        boolean size = false;
        boolean orderBy = false;
        boolean sort = false;
        for (Element member : element.getEnclosedElements()) {
            if (member.getModifiers().contains(Modifier.STATIC) || !member.getKind().isField() ||
                    member.getAnnotation(Transient.class) != null) {
                continue;
            }

            String name = member.toString();
            if ("page".equals(name)) {
                page = true;
                continue;
            }
            if ("size".equals(name)) {
                size = true;
                continue;
            }
            if ("orderBy".equals(name)) {
                orderBy = true;
                continue;
            }
            if ("sort".equals(name)) {
                sort = true;
                continue;
            }
            Criterion criterion = new Criterion();

            EqualTo equalTo = member.getAnnotation(EqualTo.class);
            if (equalTo != null) {
                criterion.setEqualTo(true);
                criterion.setFieldName("".equals(equalTo.value()) ? name : equalTo.value());
            }
            NotEqualTo notEqualTo = member.getAnnotation(NotEqualTo.class);
            if (notEqualTo != null) {
                criterion.setNotEqualTo(true);
                criterion.setFieldName("".equals(notEqualTo.value()) ? name : notEqualTo.value());
            }
            GreaterThan greaterThan = member.getAnnotation(GreaterThan.class);
            if (greaterThan != null) {
                criterion.setGreaterThan(true);
                criterion.setFieldName("".equals(greaterThan.value()) ? name : greaterThan.value());
            }
            GreaterThanOrEqualTo greaterThanOrEqualTo = member.getAnnotation(GreaterThanOrEqualTo.class);
            if (greaterThanOrEqualTo != null) {
                criterion.setGreaterThanOrEqualTo(true);
                criterion.setFieldName("".equals(greaterThanOrEqualTo.value()) ? name : greaterThanOrEqualTo.value());
            }
            In in = member.getAnnotation(In.class);
            if (in != null) {
                criterion.setIn(true);
                criterion.setFieldName("".equals(in.value()) ? name : in.value());
            }
            NotIn notIn = member.getAnnotation(NotIn.class);
            if (notIn != null) {
                criterion.setNotIn(true);
                criterion.setFieldName("".equals(notIn.value()) ? name : notIn.value());
            }
            LessThan lessThan = member.getAnnotation(LessThan.class);
            if (lessThan != null) {
                criterion.setLessThan(true);
                criterion.setFieldName("".equals(lessThan.value()) ? name : lessThan.value());
            }
            LessThanOrEqualTo lessThanOrEqualTo = member.getAnnotation(LessThanOrEqualTo.class);
            if (lessThanOrEqualTo != null) {
                criterion.setLessThanOrEqualTo(true);
                criterion.setFieldName("".equals(lessThanOrEqualTo.value()) ? name : lessThanOrEqualTo.value());
            }
            Like like = member.getAnnotation(Like.class);
            if (like != null) {
                criterion.setLike(true);
                criterion.setFieldName("".equals(like.value()) ? name : like.value());
            }
            NotLike notLike = member.getAnnotation(NotLike.class);
            if (notLike != null) {
                criterion.setNotLike(true);
                criterion.setFieldName("".equals(notLike.value()) ? name : notLike.value());
            }


            member.asType().accept(visitor, criterion);


            if (criterion.getFieldName() == null) {
                criterion.setFieldName(name);
                if (criterion.getJavaType().startsWith("java.util.List")) {
                    criterion.setIn(true);
                } else {
                    criterion.setEqualTo(true);
                }
            }

            Or or = member.getAnnotation(Or.class);
            if (or != null) {
                Criteria orCriteria = new Criteria();
                orCriteria.setOr(true);
                for (String n : or.value()) {
                    Criterion c = copyCriteria(criterion);
                    c.setFieldAliasName(n);
                    orCriteria.getCriteria().add(c);
                }
                orList.add(orCriteria);
                continue;
            }
            criterion.setFieldAliasName(name);
            criteria.getCriteria().add(criterion);
        }

        if (criteria.getCriteria().size() > 0) {
            list.add(criteria);
        }

        list.addAll(orList);

        if (list.size() > 1) {
            list.get(0).setOr(false);
        }

        metadata.setCriteria(list);
        metadata.setOrderBy(orderBy && sort);
        metadata.setPage(page && size);
        return metadata;
    }

    private Criterion copyCriteria(Criterion criterion) {
        Criterion copy = new Criterion();
        copy.setFirstUpFieldName(criterion.getFirstUpFieldName());
        copy.setFieldName(criterion.getFieldName());
        copy.setJavaType(criterion.getJavaType());
        copy.setLike(criterion.isLike());
        copy.setNotLike(criterion.isNotLike());
        copy.setIn(criterion.isIn());
        copy.setNotIn(criterion.isNotIn());
        copy.setEqualTo(criterion.isEqualTo());
        copy.setNotEqualTo(criterion.isNotEqualTo());
        copy.setGreaterThan(criterion.isGreaterThan());
        copy.setLessThan(criterion.isLessThan());
        copy.setGreaterThanOrEqualTo(criterion.isGreaterThanOrEqualTo());
        copy.setLessThanOrEqualTo(criterion.isLessThanOrEqualTo());
        copy.setFieldAliasName(criterion.getFieldAliasName());
        copy.setFirstUpFieldAliasName(criterion.getFirstUpFieldAliasName());
        return copy;

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
