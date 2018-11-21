package sample

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.*
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset
import javax.xml.parsers.DocumentBuilderFactory

class XmlToJsonConverter(private val indent: Boolean = true, private val omitQuotesFromNumbers: Boolean = true, private val omitQuotesFromBoolean: Boolean = true) {
    fun convert(sourceString: String): String? {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(IOUtils.toInputStream(sourceString, Charset.defaultCharset()))
        val root = doc.documentElement.nodeName
        val xmlMapper = XmlMapper()
        xmlMapper.registerModule(SimpleModule().addDeserializer(Any::class.java, ArrayInferringUntypedObjectDeserializer()))
        val map = xmlMapper.readValue(sourceString, Any::class.java) as Map<*,*>
        val node = xmlMapper.valueToTree<JsonNode>(map)
        val objectNode = xmlMapper.createObjectNode()
        objectNode.putObject(root)
        objectNode.set(root, node)
        val jsonMapper = ObjectMapper()
        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, indent)
        val resultNode = resolveTypeIfNeeded(objectNode)

        return jsonMapper.writer().writeValueAsString(resultNode)
    }

    private fun resolveTypeIfNeeded(jsonNode: JsonNode): JsonNode{
        return when {
            omitQuotesFromNumbers || omitQuotesFromBoolean ->  {
                resolveType(jsonNode)
            }
            else -> jsonNode
        }
    }

    private fun resolveType(jsonNode: JsonNode): JsonNode {
        @Suppress("NAME_SHADOWING") var jsonNode = jsonNode
        if (jsonNode is ObjectNode) {
            val fields = jsonNode.fields()
            while (fields.hasNext()) {
                val next = fields.next()
                next.setValue(resolveType(next.value))
            }
        } else if (jsonNode is TextNode) {
            val value = jsonNode.textValue()
            if (omitQuotesFromBoolean && "true".equals(value, ignoreCase = true) || "false".equals(value, ignoreCase = true)) {
                jsonNode = BooleanNode.valueOf(java.lang.Boolean.valueOf(value))
            } else if (omitQuotesFromNumbers && StringUtils.isNumeric(value)) {
                jsonNode = LongNode.valueOf(java.lang.Long.valueOf(value))
            }
        } else if(jsonNode is ArrayNode){
            val modifiedArray = ArrayList<JsonNode>()
            for(node in jsonNode){
                modifiedArray.add(resolveType(node))
            }
            jsonNode.removeAll()
            jsonNode.addAll(modifiedArray)
        }
        return jsonNode
    }
}