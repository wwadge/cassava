package cassava.csv.core;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Andrew Vella
 * @since 11/12/15.
 */
@RunWith(JUnit4.class)
public class CsvWriterTest {
    @Before
    public void init() {
        Mapper mapper = new Mapper();
    }

//    @Test
//    public void testWriter() {
////        List<TestClass> testClassList = new ArrayList<>();
////        testClassList.add(new TestClass());
//        TestClass testClass = new TestClass();
//        testClass.setTest("testName");
//        testClass.setTest2("testSurname");
//        TestSubClass testSubClass = new TestSubClass();
//        testSubClass.setTest3("123");
//        testClass.setTestSubClass(testSubClass);
//
//        System.out.println(CsvWriter.mapObject(testClass));
//    }
//
//
//    @Test
//    public void testWriterWithList() {
//
//        TestClassWithList listObject = new TestClassWithList();
//        listObject.setAge(10);
//
//        TestSubSubClass subSubClass = new TestSubSubClass();
//        subSubClass.setAge(11);
//        listObject.setSubClassList(Arrays.asList(subSubClass));
//
//        System.out.println(CsvWriter.mapObject(listObject));
//    }
}
