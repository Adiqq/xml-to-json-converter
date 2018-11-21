package sample

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer
import java.io.IOException
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.logging.Logger

/**
 * ArrayInferringUntypedObjectDeserializer is an deserializer for Jackson that
 * infers arrays when multiple child elements of the same name occur in the
 * XML. When deserializing JSON, an array is explicit, and Jackson handles that
 * appropriately. But Jackson can also deserialize XML, and deserializing into
 * an array from XML is not always explicit.  This class is a naive
 * implementation that infers an array when there are multiple child elements of
 * the same name in the source XML. This class does not support "wrapper"
 * elements to denote arrays.
 *
 * For example, with the default UntypedObjectDeserializer, given this XML input:
 * <pre>
 * `<Parameters>
 * <Parameter name='A'>valueA</Parameter>
 * </Parameters>
` *
</pre> *
 *
 * output:
 * <pre>
 * `[Parameters] -> (Map)
 * [Parameter] -> (Map)
 * [name] -> [A]
 * [] -> [valueA]
` *
</pre> *
 *
 * and with this input:
 * <pre>
 * `<Parameters>
 * <Parameter name='A'>valueA</Parameter>
 * <Parameter name='B'>valueB</Parameter>
 * </Parameters>
` *
</pre> *
 *
 * output:
 * <pre>
 * `[Parameters] -> (Map)
 * [Parameter] -> (Map)
 * [name] -> [B]
 * [] -> [valueB]
` *
</pre> *
 *
 * As you can see in the latter case, the second child element named Parameter
 * overwrites the previous one.
 *
 * With THIS deserializer, the result for the first input is the same. The result for the second input is:
 * <pre>
 * `[Parameters] -> (Map)
 * [Parameter] -> (List)[
 * {
 * [name] -> [A]
 * [] -> [valueA]
 * },
 * {
 * [name] -> [B]
 * [] -> [valueB]
 * }
 * ]
` *
</pre> *
 *
 * Example usage:
 * <pre>
 * `XmlMapper mapper = new XmlMapper();
 * mapper.registerModule(new SimpleModule().addDeserializer(Object.class, new ArrayInferringUntypedObjectDeserializer()));
 * XMLStreamReader sr = XMLInputFactory.newFactory().createXMLStreamReader(new FileInputStream(path));
 * Map m = (Map) mapper.readValue(sr, Object.class);
` *
</pre> *
 */
@Suppress("NAME_SHADOWING")
class ArrayInferringUntypedObjectDeserializer : UntypedObjectDeserializer(null, null) {

    @Throws(IOException::class)
    override fun mapObject(jp: JsonParser, ctxt: DeserializationContext): Map<String, Any> {
        var t = jp.currentToken
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken()
        }
        if (t == JsonToken.END_OBJECT) { // empty map, eg {}
            // empty LinkedHashMap might work; but caller may want to modify... so better just give small modifiable.
            return LinkedHashMap(2)
        }
        val result = LinkedHashMap<String, Any>()
        do {
            val fieldName = jp.currentName
            jp.nextToken()
            result[fieldName] = handleMultipleValue(result, fieldName, deserialize(jp, ctxt))
        } while (jp.nextToken() != JsonToken.END_OBJECT)
        return result
    }

    private fun handleMultipleValue(map: Map<String, Any>, key: String, value: Any): Any {
        if (!map.containsKey(key)) {
            return value
        }

        val originalValue = map[key]
        return if (originalValue is List<*>) {
            val originalValue = originalValue as MutableList<Any?>
            originalValue.add(value)
            originalValue
        } else {
            val newValue = ArrayList<Any?>()
            newValue.add(originalValue)
            newValue.add(value)
            newValue
        }
    }

    companion object {
        private val logger = Logger.getLogger(ArrayInferringUntypedObjectDeserializer::class.java.name)
    }

}