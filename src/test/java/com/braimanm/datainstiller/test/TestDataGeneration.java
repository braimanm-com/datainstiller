package com.braimanm.datainstiller.test;

import com.braimanm.datainstiller.data.*;
import com.thoughtworks.xstream.XStream;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestDataGeneration {

    private String getExpectedDataFromResource(String resourceFile) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceFile);
        assert url != null;
        String xmlActual = null;
        try {
            xmlActual = new String(Files.readAllBytes(Paths.get(url.toURI()))).replace(" ","");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return xmlActual;
    }

    @Test
    public void testDataSerialization() {
        String resourceFile = "simple_pojo_data_1.xml";
        String xmlActual = getExpectedDataFromResource(resourceFile);
        SimplePOJO simplePOJO = new SimplePOJO().fromResource(resourceFile);
        String xmlExpected = simplePOJO.toXML().replace(" ","");
        Assert.assertEquals(xmlActual.trim(), xmlExpected.trim());
    }

    @Test
    public void testDataDeSerialization() throws IllegalAccessException {
        SimplePOJO simplePOJO = new SimplePOJO().fromResource("simple_pojo_data_1.xml");
        Field[] fields = simplePOJO.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Class<?> type = field.getType();
            field.setAccessible(true);
            String actual = "" + field.get(simplePOJO);
            String expected = (i + 1) + "";
            List<?> classes = Arrays.asList(float.class, Float.class, double.class, Double.class);
            if (classes.contains(type)) {
                expected = expected + ".0";
            }
            if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                expected = "true";
            }
            if (type.equals(char.class)) {
                expected = "a";
            }
            if (type.equals(Character.class)) {
                expected = "B";
            }
            if (type.equals(String.class)) {
                expected = field.getName();
            }
            Assert.assertEquals(actual, expected);
        }
    }

    @Test
    public void testDataGenerationForSimpleArraysAndAliases() throws IllegalAccessException {
        SimplePOJOArray simplePOJOArray = new SimplePOJOArray();
        String xml = simplePOJOArray.generateXML();
        simplePOJOArray.generateData();
        String xml2 = simplePOJOArray.toXML();
        Assert.assertEquals(xml, xml2);
        Field[] fields = simplePOJOArray.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Class<?> type = field.getType();
            Assert.assertTrue(type.isArray());
            field.setAccessible(true);
            String fieldName = field.getName();

            int nArray = 3;
            if (field.isAnnotationPresent(Data.class)) {
                Data data = field.getAnnotation(Data.class);
                if (data.nArray() > 0) nArray = data.nArray();
            }
            List<String> actual = new ArrayList<>();
            List<String> expected = new ArrayList<>();
            for (int ii = 0; ii < nArray; ii++) {
                expected.add("" + i);
            }
            String expectedAliasValue = "" + i;
            Object value = field.get(simplePOJOArray);
            switch (type.getName()) {
                case "[B" :
                    byte[] bytes = (byte[]) value;
                    for (byte b : bytes) {
                        actual.add("" + b);
                    }
                    break;
                case "[Ljava.lang.Byte;" :
                    actual = Arrays.stream((Object[]) value).map(v -> "" + v).collect(Collectors.toList());
                    break;
                case "[S" :
                    short[] shorts = (short[]) value;
                    for (short sh : shorts) {
                        actual.add("" + sh);
                    }
                    break;
                case "[Ljava.lang.Short;" :
                    actual = Arrays.stream((Object[]) value).map(v -> "" + v).collect(Collectors.toList());
                    break;
                case "[I" :
                    actual = Arrays.stream((int[]) value).boxed().map(v -> "" + v).collect(Collectors.toList());
                    break;
                case "[Ljava.lang.Integer;" :
                    actual = Arrays.stream((Object[]) value).map(v -> "" + v).collect(Collectors.toList());
                    break;
                case "[J" :
                    actual = Arrays.stream((long[]) value).boxed().map(v -> "" + v).collect(Collectors.toList());
                    break;
                case "[Ljava.lang.Long;" :
                    actual = Arrays.stream((Object[]) value).map(v -> "" + v).collect(Collectors.toList());
                    break;
                case "[F" :
                    float[] floats = (float[]) value;
                    for (float fl : floats) {
                        actual.add("" + fl);
                    }
                    expected = expected.stream().map(n -> n + ".0").collect(Collectors.toList());
                    break;
                case "[Ljava.lang.Float;" :
                    actual = Arrays.stream((Object[]) value).map(v -> "" + v).collect(Collectors.toList());
                    expected = expected.stream().map(n -> n + ".0").collect(Collectors.toList());
                    break;
                case "[D" :
                    actual = Arrays.stream((double[]) value).boxed().map(v -> "" + v).collect(Collectors.toList());
                    expected = expected.stream().map(n -> n + ".0").collect(Collectors.toList());
                    break;
                case "[Ljava.lang.Double;" :
                    actual = Arrays.stream((Object[]) value).map(v -> "" + v).collect(Collectors.toList());
                    expected = expected.stream().map(n -> n + ".0").collect(Collectors.toList());
                    break;
                case "[Z" :
                    boolean[] bools = (boolean[]) value;
                    for (boolean bo : bools) {
                        actual.add("" + bo);
                    }
                    expected = expected.stream().map(n -> "true").collect(Collectors.toList());
                    expectedAliasValue = "true";
                    break;
                case "[Ljava.lang.Boolean;" :
                    actual = Arrays.stream((Object[]) value).map(v -> "" + v).collect(Collectors.toList());
                    expected = expected.stream().map(n -> "true").collect(Collectors.toList());
                    expectedAliasValue = "true";
                    break;
                case "[C" :
                    char[] chars = (char[]) value;
                    for (char ch : chars) {
                        actual.add("" + ch);
                    }
                    expected = expected.stream().map(n -> "\u0061").collect(Collectors.toList());
                    expectedAliasValue = "a";
                    break;
                case "[Ljava.lang.Character;" :
                    actual = Arrays.stream((Object[]) value).map(v -> "" + v).collect(Collectors.toList());
                    expected = expected.stream().map(n -> "a").collect(Collectors.toList());
                    expectedAliasValue = "a";
                    break;
                case "[Ljava.lang.String;" :
                    actual = Arrays.stream((Object[]) value).map(v -> "" + v).collect(Collectors.toList());
                    expected = expected.stream().map(n -> "${" + fieldName + "}").collect(Collectors.toList());
                    expectedAliasValue = fieldName;
            }
            String aliasValue;
            if (!fieldName.contains("boolean")) {
                Assert.assertTrue(simplePOJOArray.getDataAliases().containsKey(fieldName));
                aliasValue = simplePOJOArray.getDataAliases().getAsString(fieldName);
            } else {
                Assert.assertTrue(simplePOJOArray.getDataAliases().containsKey("booleanTrue"));
                aliasValue = simplePOJOArray.getDataAliases().getAsString("booleanTrue");
            }
            Assert.assertEquals(aliasValue, expectedAliasValue);
            Assert.assertEquals(actual, expected);
        }
    }

    @Test
    public void test_generic_functionality(){
        SimplePOJOArray simplePOJOArray = new SimplePOJOArray();
        String actualXml = new SimplePOJOArray().generateXML();
        SimplePOJOArray expected = simplePOJOArray.fromXml(actualXml,false);
        Assert.assertEquals(actualXml, expected.toXML());
        SimplePOJOArray expected2 = new SimplePOJOArray();
        expected2.generateData();
        Assert.assertEquals(actualXml, expected2.toXML());

        SimplePOJOArray actual3 = simplePOJOArray.fromXml(actualXml,true);
        SimplePOJOArray expected3 = new SimplePOJOArray().fromResource("simple_pojo_array_data.xml", true);
        Assert.assertEquals(actual3.toXML(), expected3.toXML());
    }

    @Test
    public void test_Circular_References() {
        CircularReferenceA a = new CircularReferenceA();
        String xml = a.generateXML();
        CircularReferenceA aa = new CircularReferenceA().fromXml(xml);
        Assert.assertEquals(xml.split("<CLASS-A>").length - 1, 13);
        Assert.assertEquals(xml, aa.toXML());
        DataGenerator generator = new DataGenerator(new XStream());
        generator.setRecursionLevel(3);
        a = generator.generate(CircularReferenceA.class);
        Assert.assertEquals(a.toXML().split("<CLASS-A>").length - 1, 19);

    }

    @Test
    public void test_multiple_data_references() {
        String xmlExpected = getExpectedDataFromResource("GenericConsumerData.xml").trim();
        GenericDataConsumer data = new GenericDataConsumer();
        data.generateData();
        String xmlActual = data.toXML().replace(" ","").trim();
        Assert.assertEquals(xmlActual, xmlExpected);
    }

}
