package org.telosys.tools.dsl.parser;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;

public class FieldParserTest {
    @Test
    public void testParseFieldValid() throws Exception {
        String fieldInfo = "id:integer";

        DomainEntityField compareTo = new DomainEntityField("id", DomainNeutralTypes.getType("integer"));

        FieldParser fieldParser = new FieldParser(new DomainModel("model"));
        Assert.assertEquals(compareTo, fieldParser.parseField("NoRealFile", fieldInfo));
    }

    @Test(expected = EntityParserException.class)
    public void testParseFieldWithoutType() throws Exception {
        String fieldInfo = "id:";

        FieldParser fieldParser = new FieldParser(new DomainModel("model"));
        fieldParser.parseField("NoRealFile", fieldInfo);
    }

    @Test(expected = EntityParserException.class)
    public void testParseFieldWithoutName() throws Exception {
        String fieldInfo = ":integer";

        FieldParser fieldParser = new FieldParser(new DomainModel("model"));
        fieldParser.parseField("NoRealFile", fieldInfo);
    }

    @Test()
    public void testParseFieldWithAnnotation() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        String fieldInfo = "id:integer{@Id}";
        
        DomainEntityField expectedField = new DomainEntityField("id", DomainNeutralTypes.getType("integer"));
        List<DomainEntityFieldAnnotation> annotationList = new ArrayList<DomainEntityFieldAnnotation>();
        annotationList.add(new DomainEntityFieldAnnotation("Id"));
        expectedField.setAnnotationList(annotationList);

        FieldParser fieldParser = new FieldParser(new DomainModel("model"));

        //mock annotationParser
        AnnotationParser mockAnnotationParser = EasyMock.createMock(AnnotationParser.class);
        EasyMock.expect(mockAnnotationParser.parseAnnotations("EntityForTest", "id:integer{@Id}")).andReturn(annotationList);
        java.lang.reflect.Field field = fieldParser.getClass().getDeclaredField("annotationParser");
        field.setAccessible(true);
        field.set(fieldParser, mockAnnotationParser);
        EasyMock.replay(mockAnnotationParser);

        Assert.assertEquals(expectedField, fieldParser.parseField("EntityForTest", fieldInfo));
        EasyMock.verify(mockAnnotationParser);
    }

//    @Test
//    public void testParseFieldWithEnum() {
//        String fieldInfo = "id:#Gender";
//
//        DomainModel model = new DomainModel("model");
//        DomainEnumerationForString domainEnumeration = new DomainEnumerationForString("Gender");
//        model.addEnumeration(domainEnumeration);
//
//        DomainEntityField compareTo = new DomainEntityField("id", domainEnumeration);
//
//        FieldParser fieldParser = new FieldParser(model);
//        Assert.assertEquals(compareTo, fieldParser.parseField(fieldInfo));
//    }
    
    @Test
    public void testParseFieldWithEntityReferenced() {
        String fieldInfo = "id:Country";

        DomainModel model = new DomainModel("model");
        DomainEntity country = new DomainEntity("Country");
        model.addEntity(country);

        DomainEntityField compareTo = new DomainEntityField("id", country);

        FieldParser fieldParser = new FieldParser(model);
        Assert.assertEquals(compareTo, fieldParser.parseField("NoRealFile", fieldInfo));
    }

    @Test
    public void testParseFieldWithCardinalityN() throws Exception {
        String fieldInfo = "id:integer[]";

        DomainEntityField compareTo = new DomainEntityField("id", DomainNeutralTypes.getType("integer"), -1);

        FieldParser fieldParser = new FieldParser(new DomainModel("model"));
        Assert.assertEquals(compareTo, fieldParser.parseField("NoRealFile", fieldInfo));
    }

    @Test
    public void testParseFieldWithCardinality3() throws Exception {
        String fieldInfo = "id:integer[3]";

        DomainEntityField compareTo = new DomainEntityField("id", DomainNeutralTypes.getType("integer"), 3);

        FieldParser fieldParser = new FieldParser(new DomainModel("model"));
        Assert.assertEquals(compareTo, fieldParser.parseField("NoRealFile", fieldInfo));
    }

    @Test
    public void testParseFieldWithoutCardinality() throws Exception {
        String fieldInfo = "id:integer";

        DomainEntityField compareTo = new DomainEntityField("id", DomainNeutralTypes.getType("integer"), 1);

        FieldParser fieldParser = new FieldParser(new DomainModel("model"));
        Assert.assertEquals(compareTo, fieldParser.parseField("NoRealFile", fieldInfo));
    }

    @Test(expected = EntityParserException.class)
    public void testParseFieldWithBadCardinality() throws Exception {
        String fieldInfo = "id:integer[n]";
        FieldParser fieldParser = new FieldParser(new DomainModel("model"));
        fieldParser.parseField("NoRealFile", fieldInfo);
    }

    @Test(expected = EntityParserException.class)
    public void testParseFieldWithUnknownEnum() {
        String fieldInfo = "id:#Gender";

        DomainModel model = new DomainModel("model");

        FieldParser fieldParser = new FieldParser(model);
        fieldParser.parseField("NoRealFile", fieldInfo);
    }

    @Test(expected = EntityParserException.class)
    public void testParseFieldWithUnknown() {
        String fieldInfo = "id:Country";

        DomainModel model = new DomainModel("model");

        FieldParser fieldParser = new FieldParser(model);
        fieldParser.parseField("NoRealFile", fieldInfo);
    }

    @Test(expected = EntityParserException.class)
    public void testParseWithWrongTypeSeparator() {
        String fieldInfo = "id=integer";

        DomainModel model = new DomainModel("model");

        FieldParser fieldParser = new FieldParser(model);
        fieldParser.parseField("NoRealFile", fieldInfo);
    }

    @Test(expected = EntityParserException.class)
    public void testParseWithoutTypeSeparator() {
        String fieldInfo = "idinteger";

        DomainModel model = new DomainModel("model");

        FieldParser fieldParser = new FieldParser(model);
        fieldParser.parseField("NoRealFile", fieldInfo);
    }
}
