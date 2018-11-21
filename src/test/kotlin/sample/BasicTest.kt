package sample

import org.apache.commons.io.IOUtils
import org.junit.Test
import org.junit.Assert.*

class BasicTest {
        
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
        val result = converter.convert(xml)
        assertEquals(json,result)
    }
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
        val result = converter.convert(xml)
        assertEquals(json,result)
    }
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
        val result = converter.convert(xml)
        assertEquals(json,result)
    }
}