package com.vcg.mybatis.example.processor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.vcg.mybatis.example.processor.converter.annotation.*;
import com.vcg.mybatis.example.processor.domain.*;
import com.vcg.mybatis.example.processor.util.CamelUtils;
import com.vcg.mybatis.example.processor.visitor.DomainTypeVisitor;
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
import javax.lang.model.util.Elements;
import javax.persistence.*;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import org.apache.ibatis.type.TypeHandler;

@SupportedAnnotationTypes("com.vcg.mybatis.example.processor.Example")
public class MybatisDomainProcessor extends AbstractProcessor {

    private final DomainTypeVisitor domainTypeVisitor = new DomainTypeVisitor();
    private final QueryTypeVisitor queryTypeVisitor = new QueryTypeVisitor();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Filer filer = processingEnv.getFiler();
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Example.class);
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
            ClassLoader classLoader = MybatisDomainProcessor.class.getClassLoader();

            for (Element element : elements) {
                try {
                    TableMetadata tableMetadata = readTableMetadata(element);
                    String exampleName = tableMetadata.getExampleClazzName();

                    JavaFileObject javaFileObject = filer.createSourceFile(exampleName);
                    HashMap<String, Object> scopes = new HashMap<>();
                    scopes.put("metadata", tableMetadata);


                    InputStream exampleInputStream = classLoader.getResourceAsStream("templates/Example.java");
                    try (InputStreamReader in = new InputStreamReader(exampleInputStream, StandardCharsets.UTF_8); Writer writer = javaFileObject.openWriter()) {
                        Mustache mustache = mf.compile(in, exampleName);
                        mustache.execute(writer, scopes);
                    }

                    PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(element);
                    String xml = tableMetadata.getDomainClazzSimpleName() + "ExampleMapper.xml";
                    FileObject xmlOut = filer.createResource(StandardLocation.CLASS_OUTPUT, packageOf.toString(), xml);
                    InputStream xmlInputStream = classLoader.getResourceAsStream("templates/Example.xml");
                    try (InputStreamReader in = new InputStreamReader(xmlInputStream, StandardCharsets.UTF_8);
                         Writer writer = xmlOut.openWriter()) {
                        Mustache mustache = mf.compile(in, tableMetadata.getDomainClazzName() + ".xml");
                        mustache.execute(writer, scopes);
                    }

                    Example query = element.getAnnotation(Example.class);

                    if (!query.query()) {
                        continue;
                    }
                    QueryMetadata queryMetadata = readQueryMetadata(element);
                    String queryName = queryMetadata.getQueryClazzName();
                    JavaFileObject queryJavaFileObject = filer.createSourceFile(queryName);
                    scopes.put("query", queryMetadata);
                    InputStream queryInputStream = classLoader.getResourceAsStream("templates/Query.java");
                    try (InputStreamReader in = new InputStreamReader(queryInputStream, StandardCharsets.UTF_8);
                         Writer writer = queryJavaFileObject.openWriter();
                         FileWriter writer2 = new FileWriter("/Users/wuyu/Desktop/error.txt")) {
                        Mustache mustache = mf.compile(in, queryName);
                        mustache.execute(writer, scopes);
                        mustache.execute(writer2, scopes);
                    }
                } catch (Exception e) {
                    try (StringWriter writer = new StringWriter();
                         PrintWriter printWriter = new PrintWriter(writer)) {
                        e.printStackTrace(printWriter);
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, writer.toString());
                    }
                }
            }


        } catch (Exception e) {
            try (StringWriter writer = new StringWriter();
                 PrintWriter printWriter = new PrintWriter(writer)) {
                e.printStackTrace(printWriter);
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, writer.toString());
            } catch (IOException ioException) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
            }
        }
        return true;
    }


    public TableMetadata readTableMetadata(Element element) {
        Example example = element.getAnnotation(Example.class);
        Table table = element.getAnnotation(Table.class);
        PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(element);
        String clazzName = element.toString();

        String exampleName = (clazzName + "Example");

        TableMetadata tableMetadata = new TableMetadata()
                .setDomainClazzName(clazzName)
                .setExampleClazzName(exampleName)
                .setPackageName(packageOf.toString())
                .setShard(null);

        String repositoryName = !example.namespace().equals("") ? example.namespace() :
                exampleName + "." + tableMetadata.getExampleClazzSimpleName() + "Repository";
        tableMetadata.setRepositoryClazzName(repositoryName)
                .setTableName(table != null ? table.name() : String.join("_",
                        CamelUtils.split(tableMetadata.getDomainClazzSimpleName(), true)));
        Elements elementUtils = processingEnv.getElementUtils();

        for (Element member : element.getEnclosedElements()) {
            if (member.getModifiers().contains(Modifier.STATIC) || !member.getKind().isField() ||
                    member.getAnnotation(Transient.class) != null ||
                    member.getAnnotation(ManyToOne.class) != null) {
                continue;
            }

            String name = member.toString();
            JoinColumn joinColumn = member.getAnnotation(JoinColumn.class);
            OneToOne oneToOne = member.getAnnotation(OneToOne.class);
            OneToMany oneToMany = member.getAnnotation(OneToMany.class);
            ManyToMany manyToMany = member.getAnnotation(ManyToMany.class);

            if (joinColumn != null) {
                if (!"".equals(joinColumn.name())) {
                    JoinMetadata joinMetadata = new JoinMetadata()
                            .setColumnName(joinColumn.name())
                            .setFieldName(name);
                    if (oneToOne != null) {
                        joinMetadata.setMappedBy(oneToOne.mappedBy())
                                .setFetchType(oneToOne.fetch().name().toLowerCase());
                        tableMetadata.getOneToOne().add(joinMetadata);
                    }

                    if (oneToMany != null) {
                        joinMetadata.setMappedBy(oneToMany.mappedBy())
                                .setFetchType(oneToMany.fetch().name().toLowerCase());
                        tableMetadata.getOneToMany().add(joinMetadata);
                    }

                    if (manyToMany != null) {
                        joinMetadata.setMappedBy(manyToMany.mappedBy())
                                .setFetchType(manyToMany.fetch().name().toLowerCase());
                        tableMetadata.getOneToMany().add(joinMetadata);
                    }
                }
                continue;
            }


            Id id = member.getAnnotation(Id.class);
            Column column = member.getAnnotation(Column.class);
            GeneratedValue generatedValue = member.getAnnotation(GeneratedValue.class);
            ColumnMetadata columnMetadata = new ColumnMetadata();
            member.asType().accept(domainTypeVisitor, columnMetadata);
            columnMetadata.setFieldName(name)
                    .setUseGeneratedKeys(generatedValue != null)
                    .setPrimary(id != null);


            if (column == null || "".equals(column.name())) {
                columnMetadata.setColumnName(String.join("_", CamelUtils.split(name, true)));
            }

            if (column != null && !"".equals(column.columnDefinition())) {
                String jdbcType = JDBC_TYPE_MAPPING.get(column.columnDefinition().replaceAll("\\s+", " ").toUpperCase());
                columnMetadata.setJdbcType(jdbcType != null ? jdbcType : column.columnDefinition());
            }

            if (column != null && !"".equals(column.name())) {
                columnMetadata.setColumnName(column.name());
            }

            if (id != null) {
                tableMetadata.setPrimaryMetadata(columnMetadata);
            }

            Convert annotation = member.getAnnotation(Convert.class);
            if (annotation != null && TypeHandler.class.isAssignableFrom(annotation.converter())) {
                String typeHandler = annotation.converter().getName();
                columnMetadata.setTypeHandler(typeHandler);
            }

            String docComment = elementUtils.getDocComment(member);
            columnMetadata.setJavaDoc(docComment);
            tableMetadata.getColumnMetadataList().add(columnMetadata);
        }

        String columns = tableMetadata.getColumnMetadataList()
                .stream()
                .map(ColumnMetadata::getColumnName)
                .collect(Collectors.joining(", "));

        return tableMetadata.setColumns(columns);
    }


    public QueryMetadata readQueryMetadata(Element element) {
        PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(element);
        String packageName = packageOf.toString();
        String clazzName = element.toString();

        String queryName = (clazzName + "Query");
        String exampleName = (clazzName + "Example");
        QueryMetadata metadata = new QueryMetadata()
                .setQueryClazzName(queryName)
                .setExampleClazzName(exampleName)
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

            member.asType().accept(queryTypeVisitor, criterion);

            if (criterion.getFieldName() == null) {
                continue;
            }

            if (criterion.getJavaType().startsWith("java.util.List")) {
                criterion.setIn(true);
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

    private static final Map<String, String> JDBC_TYPE_MAPPING = new HashMap<>();

    static {
        JDBC_TYPE_MAPPING.put("INT", "INTEGER");
        JDBC_TYPE_MAPPING.put("INT UNSIGNED", "INTEGER");
        JDBC_TYPE_MAPPING.put("SMALLINT UNSIGNED", "SMALLINT");
        JDBC_TYPE_MAPPING.put("BIGINT UNSIGNED", "BIGINT");
        JDBC_TYPE_MAPPING.put("DOUBLE UNSIGNED", "DOUBLE");
        JDBC_TYPE_MAPPING.put("FLOAT UNSIGNED", "DOUBLE");
        JDBC_TYPE_MAPPING.put("DECIMAL UNSIGNED", "DECIMAL");
        JDBC_TYPE_MAPPING.put("TINY UNSIGNED", "TINY");
        JDBC_TYPE_MAPPING.put("TEXT", "LONGVARCHAR");
        JDBC_TYPE_MAPPING.put("TINYTEXT", "VARCHAR");
        JDBC_TYPE_MAPPING.put("MEDIUMTEXT", "LONGVARCHAR");
        JDBC_TYPE_MAPPING.put("LONGTEXT", "LONGVARCHAR");
        JDBC_TYPE_MAPPING.put("DATETIME", "TIMESTAMP");
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
