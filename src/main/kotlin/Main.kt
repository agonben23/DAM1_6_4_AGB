package un6.eje6_4

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.lang.Exception
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import java.util.logging.Level
import java.util.logging.LogManager

val l = LogManager.getLogManager().getLogger("").apply { level = Level.ALL }

/** @constructor Es un catálogo de libros que trabaja con archivos XML como base de datos
 * @param cargador Es un String con la ruta del archivo XML
 * @property lista Almacena la lista de nodos */

class CatalogoLibrosXML(cargador: String) {

    init {
        try {
            val xmlDoc = readXml(cargador)
        }   catch (FileNotFoundException : Exception) {
            l.severe("Archivo no encontrado")
        }
    }

    private val lista = obtenerListaNodosPorNombre(readXml(cargador), "book")

    /**
     * @param idLibro Es un String que indica la id del libro a buscar
     * @return Devuelve un boolean que determina si existe un libro con la id introducida*/

    fun existeLibro(idLibro: String): Boolean {


        val libro: Map<String, String>?
        val listLibros = arrayListOf<Map<String, String>>()
        lista.forEach {
            listLibros.add(obtenerAtributosEnMapKV(it as Element))
        }
        libro = listLibros.find { it.containsValue(idLibro) }
        return libro != null
    }

    /**
     * @param idLibro Es un String que indica la id del libro a buscar
     * @return Devuelve un mapa con la info del libro en caso */

    fun infoLibro(idLibro:String): Map<String,Any>{

        val mapeoElementos = mutableMapOf<String,Node>()
        lista.forEach{
            val elemento = it as Element
            val id = obtenerAtributosEnMapKV(elemento).getValue("id")
            mapeoElementos.putIfAbsent(id,elemento)
        }
        val libro = mapeoElementos.getValue(idLibro)
        val mapaInfo = mutableMapOf<String,Any>()
        mapaInfo.putIfAbsent("id",obtenerAtributosEnMapKV(libro as Element).getValue("id"))
        mapaInfo.putIfAbsent("author",libro.getElementsByTagName("author").item(0).textContent)
        mapaInfo.putIfAbsent("title",libro.getElementsByTagName("title").item(0).textContent)
        mapaInfo.putIfAbsent("genre",libro.getElementsByTagName("genre").item(0).textContent)
        mapaInfo.putIfAbsent("price",libro.getElementsByTagName("price").item(0).textContent.toDouble())
        val dateList = libro.getElementsByTagName("publish_date").item(0).textContent.split("-")
        mapaInfo.putIfAbsent("publish_date",Date(dateList[0].toInt(),dateList[1].toInt(),dateList[2].toInt()))
        mapaInfo.putIfAbsent("description",libro.getElementsByTagName("description").item(0).textContent)
        return mapaInfo
    }

    /**
     * @property[pathName] Es un String con la ruta del archivo del XML
     * @return Devuelve un documento normalizado con los elementos del XML*/

    private fun readXml(pathName: String): Document {

        val xmlFile = File(pathName)
        xmlFile.normalize()
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
    }

    /**
     * @property[e] Es un elemento del XML
     * @return Devuelve un MutableMap con los atributos del elemento*/

    private fun obtenerAtributosEnMapKV(e: Element): MutableMap<String, String> {

        val mMap = mutableMapOf<String, String>()
        for (j in 0..e.attributes.length - 1)
            mMap.putIfAbsent(e.attributes.item(j).nodeName, e.attributes.item(j).nodeValue)
        return mMap
    }

    /**
     * @property[doc] Es el documento obtenido con los elementos del XML gracias a la función [readXml]
     * @property[tagName] Es un String con el nombre de la etiqueta a buscar
     * @return Devuelve una MutableList con el conjunto de nodos con el [tagName] que conforman el documento [doc]*/

    private fun obtenerListaNodosPorNombre(doc: Document, tagName: String): MutableList<Node> {

        val bookList: NodeList = doc.getElementsByTagName(tagName)
        val lista = mutableListOf<Node>()
        for (i in 0..bookList.length - 1)
            lista.add(bookList.item(i))
        return lista
    }

}

fun main() {
    val catalogo = CatalogoLibrosXML("..\\ejercicio5_4_AGB\\src\\main\\kotlin\\catalogo.xml")

    l.info("${catalogo.existeLibro("bk101")}")
    l.info("${catalogo.infoLibro("bk101")}")
}