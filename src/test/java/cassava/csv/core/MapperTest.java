package cassava.csv.core;

import cassava.csv.core.objects.TestClass;
import cassava.csv.core.typemappers.LocalDateTypeMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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


    private static final String TEST_STRING =
            "name,position,surname,inttest,datetest,age\ntest1,positiontest,surname,1,2015-11-02,10\ntest2,positiontest,surname,1,2015-11-02,10";

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
    public void testMappingWithDifferetDelimiter() {
        String headersAndData = "name|position|surname|inttest|datetest|age\ntest1|positiontest|surname|1|2015-11-02|10";
        StringReader reader = new StringReader(headersAndData);
        mapper = new Mapper("|");
        List<TestClass> results = new ArrayList<>();
        mapper.map(reader, TestClass.class, false,results::add);
        assertTrue(!results.isEmpty());
        assertTrue(results.size() == 1);
    }


}
