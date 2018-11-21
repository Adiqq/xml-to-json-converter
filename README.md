# xml-to-json-converter
Kotlin XML to JSON converter

[Inspired by](https://github.com/DinoChiesa/deserialize-xml-arrays-jackson)

[Code](src/main/kotlin/sample/XmlToJsonConverter.kt)

Input:
```xml
<test>
    <inside>
        <someString>string</someString>
        <someNumber>123</someNumber>
        <someBoolean>true</someBoolean>
        <someOtherNumber>123.45</someOtherNumber>
    </inside>
    <someArray>
        <arrayItem>1</arrayItem>
        <arrayItem>2</arrayItem>
        <arrayItem>3</arrayItem>
    </someArray>
    <complexArray>
        <complexItem>
            <name>test1</name>
            <value>1</value>
            <enabled>true</enabled>
        </complexItem>
        <complexItem>
            <name>test2</name>
            <value>2</value>
            <enabled>false</enabled>
        </complexItem>
        <complexItem>
            <name>test3</name>
            <value>3</value>
            <enabled>true</enabled>
        </complexItem>
    </complexArray>
</test>
```
## Case - omit quotes from numbers and boolean
### Code
```kotlin
@Test
    fun testXmltoJsonConversion_omit_both() {
        val converter = XmlToJsonConverter()
        val xml = IOUtils.toString(
                this::class.java.getResourceAsStream("/test.xml"),
                "UTF-8"
        )
        val json = IOUtils.toString(
                this::class.java.getResourceAsStream("/test_omitted_both.json"),
                "UTF-8"
        )
        val result = converter.convert(xml)!!.replace("\r\n", "\n")
        assertEquals(json,result)
    }
```
### Output
```json
{
  "test" : {
    "inside" : {
      "someString" : "string",
      "someNumber" : 123,
      "someBoolean" : true,
      "someOtherNumber" : "123.45"
    },
    "someArray" : {
      "arrayItem" : [ 1, 2, 3 ]
    },
    "complexArray" : {
      "complexItem" : [ {
        "name" : "test1",
        "value" : 1,
        "enabled" : true
      }, {
        "name" : "test2",
        "value" : 2,
        "enabled" : false
      }, {
        "name" : "test3",
        "value" : 3,
        "enabled" : true
      } ]
    }
  }
}
```

## Case - omit quotes from numbers only
### Code
```kotlin
@Test
    fun testXmltoJsonConversion_omit_booleans_false() {
        val converter = XmlToJsonConverter(omitQuotesFromBoolean = false)
        val xml = IOUtils.toString(
                this::class.java.getResourceAsStream("/test.xml"),
                "UTF-8"
        )
        val json = IOUtils.toString(
                this::class.java.getResourceAsStream("/test_omitted_booleans.json"),
                "UTF-8"
        )
        val result = converter.convert(xml)!!.replace("\r\n", "\n")
        assertEquals(json,result)
    }
```
### Output
```json
{
  "test" : {
    "inside" : {
      "someString" : "string",
      "someNumber" : 123,
      "someBoolean" : "true",
      "someOtherNumber" : "123.45"
    },
    "someArray" : {
      "arrayItem" : [ 1, 2, 3 ]
    },
    "complexArray" : {
      "complexItem" : [ {
        "name" : "test1",
        "value" : 1,
        "enabled" : "true"
      }, {
        "name" : "test2",
        "value" : 2,
        "enabled" : false
      }, {
        "name" : "test3",
        "value" : 3,
        "enabled" : "true"
      } ]
    }
  }
}
```

## Case - omit quotes from booleans only
### Code
```kotlin
@Test
    fun testXmltoJsonConversion_omit_numbers_false() {
        val converter = XmlToJsonConverter(omitQuotesFromNumbers = false)
        val xml = IOUtils.toString(
                this::class.java.getResourceAsStream("/test.xml"),
                "UTF-8"
        )
        val json = IOUtils.toString(
                this::class.java.getResourceAsStream("/test_omitted_numbers.json"),
                "UTF-8"
        )
        val result = converter.convert(xml)!!.replace("\r\n", "\n")
        assertEquals(json,result)
    }
```
### Output
```json
{
  "test" : {
    "inside" : {
      "someString" : "string",
      "someNumber" : "123",
      "someBoolean" : true,
      "someOtherNumber" : "123.45"
    },
    "someArray" : {
      "arrayItem" : [ "1", "2", "3" ]
    },
    "complexArray" : {
      "complexItem" : [ {
        "name" : "test1",
        "value" : "1",
        "enabled" : true
      }, {
        "name" : "test2",
        "value" : "2",
        "enabled" : false
      }, {
        "name" : "test3",
        "value" : "3",
        "enabled" : true
      } ]
    }
  }
}
```
