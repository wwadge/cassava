# Cassava

This project provides simple translations from CSV files to Java Pojos.

## Usage
### Instansiating Mapper
Simply create a new instance of the Mapper class and call the map method. The map method requires the following:
* A Java Reader
* Java class to which to map to
* Boolean Flag indicating whether to use headers or not.

###Annotating classes
* Simply annotate your class with the @CsvType annotation and its respective fields with the @CsvField annotation.

####Using CSV headers
* When using headers the first line of the CSV file must always contain the header names. Each field within your class must then be annotated with the @CsvField annotation with the headerName value set to the respective header. The ignoreHeaders flag when calling the map method must be set to false.
```
    @CsvField(headerName = "name")
    private String name;
```

####Using column position
* When using positional parsing the Mapper class must be told to ignoreHeaders via the respective flag . Each field within your class must then be annotated with the @CsvField annotation with the columnPosition value set to the respective positiion.
```
    @CsvField(columnPosition = 1)
    private String name;
```

####Embedded classes
* Embedded classes must also be annotated with the @CsvType annotation and their respective fields with the @CsvField annotation (headername or position). The property in the parent class containing this embedded class just needs to be annotated with the @CsvField
```
    @CsvField()
    private TestSubClass testSubClass;
```
