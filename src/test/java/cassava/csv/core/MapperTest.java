package cassava.csv.core;

import cassava.csv.core.exceptions.ConversionException;
import cassava.csv.core.objects.*;
import cassava.csv.core.typemappers.LocalDateTypeMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Andrew Vella
 * @since 04/11/15.
 */
@RunWith(JUnit4.class)
public class MapperTest {


    private Mapper mapper;
    @Before
    public void init() {
        mapper = new Mapper();
    }

    @Test
    public void testHeaderMappingReturningIterator() {
        String headersAndData = "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10";
        StringReader reader = new StringReader(headersAndData);
        Iterator<TestClass> iterator = mapper.map(reader, TestClass.class, false);
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
    }

    @Test
    public void testPositionMappingReturningIterator() {
        String headersAndData = "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10";
        StringReader reader = new StringReader(headersAndData);
        Iterator<TestClass> iterator = mapper.map(reader, TestClass.class, true);
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());

    }

    @Test
    public void testStringMappingWithHeader() {
        String headersAndData = "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10";
        StringReader reader = new StringReader(headersAndData);
        Iterator<TestClass> iterator = mapper.map(reader, TestClass.class, false);
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        TestClass testClass = iterator.next();
        assertTrue(testClass.getTest().equals("test1"));
    }

    @Test
    public void testStringMappingWithPosition() {
        String headersAndData = "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10";
        StringReader reader = new StringReader(headersAndData);
        Iterator<TestClass> iterator = mapper.map(reader, TestClass.class, false);
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        TestClass testClass = iterator.next();
        assertTrue(testClass.getTest2().equals("positiontest"));
    }

    @Test
    public void testIntMappingWithHeader() {
        String headersAndData = "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10";
        StringReader reader = new StringReader(headersAndData);
        Iterator<TestClass> iterator = mapper.map(reader, TestClass.class, false);
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        TestClass testClass = iterator.next();
        assertTrue(testClass.getTestInt() == 1);
    }


    @Test
    public void testEmbeddedClassPopulatedWithHeader() {
        String headersAndData = "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10";
        StringReader reader = new StringReader(headersAndData);
        Iterator<TestClass> iterator = mapper.map(reader, TestClass.class, false);
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        TestClass testClass = iterator.next();
        assertNotNull(testClass.getTestSubClass());
        assertTrue(testClass.getTestSubClass().getTest3().equals("surname"));
    }


    @Test
    public void testRecursiveEmbeddedClassPopulatedWithHeader() {
        String headersAndData = "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10";
        StringReader reader = new StringReader(headersAndData);
        Iterator<TestClass> iterator = mapper.map(reader, TestClass.class, false);
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        TestClass testClass = iterator.next();
        assertNotNull(testClass.getTestSubClass());
        assertTrue(testClass.getTestSubClass().getTest3().equals("surname"));
        assertNotNull(testClass.getTestSubClass().getSubSubClass());
        assertTrue(testClass.getTestSubClass().getSubSubClass().getAge() == 10);
    }


    @Test
    public void testLocalDateMappingWithHeader() {
        String headersAndData = "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10";
        StringReader reader = new StringReader(headersAndData);
        Iterator<TestClass> iterator = mapper.map(reader, TestClass.class, false);
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        TestClass testClass = iterator.next();
        LocalDateTypeMapper mapper = new LocalDateTypeMapper();
        assertTrue(testClass.getTestDate().equals(mapper.fromString("2015-11-02")));
    }

    @Test
    public void testMappingWithFunction() {
        String headersAndData = "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10";
        StringReader reader = new StringReader(headersAndData);
        List<TestClass> results = new ArrayList<>();
        mapper.map(reader, TestClass.class, false,results::add);
        assertTrue(!results.isEmpty());
        assertTrue(results.size() == 1);
    }


    @Test
    public void testMappingWithDifferentDelimiter() {
        String headersAndData = "name|position|surname|inttest|datetest|age\ntest1|positiontest|surname|1|2015-11-02|10";
        StringReader reader = new StringReader(headersAndData);
        mapper = new Mapper("|");
        List<TestClass> results = new ArrayList<>();
        mapper.map(reader, TestClass.class, false,results::add);
        assertTrue(!results.isEmpty());
        assertTrue(results.size() == 1);
    }


    @Test
    public void testMappingWithSameHeader() {
        String headersAndData = "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10";
        StringReader reader = new StringReader(headersAndData);
        List<TestClassSameHeader> results = new ArrayList<>();
        mapper.map(reader, TestClassSameHeader.class, false,results::add);
        assertTrue(!results.isEmpty());
        assertTrue(results.size() == 1);
    }
    @Test
    public void testMappingWithList() {
        String headersAndData = "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10";
        StringReader reader = new StringReader(headersAndData);
        List<TestClassWithList> results = new ArrayList<>();
        mapper.map(reader, TestClassWithList.class, false,results::add);
        assertTrue(!results.isEmpty());
        assertTrue(results.size() == 1);
        assertTrue(!results.stream().findFirst().get().getSubClassList().isEmpty());
    }


    @Test
    public void testMappingWithFunctionForMapping() {

        String headersAndData = "name,position,surname,inttest,datetest,age,flag\ntest1,positiontest,surname,1,2015-11-02,10,true" +
                "\ntest2,positiontest,surname,1,2015-11-02,10,false";
        StringReader reader = new StringReader(headersAndData);
        List<Object> results = new ArrayList<>();
        mapper.map(reader, Object.class, false, results::add, csvDataFields -> {
            for(CsvDataField csvDataField : csvDataFields) {
                if(csvDataField.getHeaderName()!= null && !csvDataField.getHeaderName().isEmpty() && csvDataField.getHeaderName().equals("flag") && Boolean.valueOf(csvDataField.getFieldValue())) {
                    return TestClassBWithFlag.class;
                }
            }
            return TestClassB.class;
        });
        assertTrue(!results.isEmpty());

        boolean objectAFound = false;
        boolean objectBFound = false;
        for(Object result : results) {
            if(result instanceof TestClassB) {
                objectAFound = true;
            }
            if(result instanceof TestClassBWithFlag) {
                objectBFound = true;
            }
        }
        assertTrue(objectAFound);
        assertTrue(objectBFound);
    }


    @Test
    public void testMappingWithEmptyValues() {

        String headersAndData = "name,position,surname,inttest,datetest,age,flag\n,,,,,,,,,,,,,,,,,,";
        StringReader reader = new StringReader(headersAndData);
        List<TestClass> results = new ArrayList<>();
        mapper.map(reader, TestClass.class, false,results::add);
        assertTrue(!results.isEmpty());
        assertTrue(results.size() == 1);
    }


    @Test
    public void testMapPopulation() {
        String headersAndData = "age,[flag]\n1,test";
        StringReader reader = new StringReader(headersAndData);
        List<TestClassWithMap> results = new ArrayList<>();
        mapper.map(reader, TestClassWithMap.class, false,results::add);
        assertTrue(!results.isEmpty());
        assertTrue(results.size() == 1);
    }

    @Test(expected = ConversionException.class)
    public void testWriterWithoutPositionAnnotation() {
        TestClass testClass = new TestClass();
        testClass.setTest("testName");
        testClass.setTest2("testSurname");
        mapper.mapToString(testClass,true);
    }

    @Test
    public void testWriterWithProperAnnotation() {
        TestClassB testClass = new TestClassB();
        testClass.setSurname("testSurname");
        String result = mapper.mapToString(testClass,true);
        assertTrue(result.contains("testSurname"));
    }

    @Test
    public void testWriterWithOrder() {
        TestWriteClass testClass = new TestWriteClass();
        testClass.setSurname("testSurname");
        testClass.setName("testName");
        testClass.setAge(10);
        String result = mapper.mapToString(testClass,true);
        assertNotNull(result);
        String [] testArray = result.split(",");
        assertTrue(testArray.length == 3);
        assertEquals(testArray[0],"testName");
        assertEquals(testArray[1],"10");
        assertEquals(testArray[2],"testSurname");
    }


    @Test
    public void testWriterWithOrderWitEembedded() {
        TestWriteClassB testClass = new TestWriteClassB();
        testClass.setSurname("testSurname");
        testClass.setName("testName");
        TestClassBWithFlag testClassBWithFlag = new TestClassBWithFlag();
        testClassBWithFlag.setAge(11);
        testClassBWithFlag.setSurname("testSurname2");
        testClass.setTestClassBWithFlag(testClassBWithFlag);
        String result = mapper.mapToString(testClass,true);
        assertNotNull(result);
        String [] testArray = result.split(",");
        assertTrue(testArray.length == 4);
        assertEquals(testArray[0],"testName");
        assertEquals(testArray[1],"11");
        assertEquals(testArray[2],"testSurname2");
        assertEquals(testArray[3],"testSurname");
    }














}
