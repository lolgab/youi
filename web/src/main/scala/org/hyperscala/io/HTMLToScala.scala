package org.hyperscala.io

import org.hyperscala.html._
import org.jdom2.input.SAXBuilder
import java.io.{FileWriter, File, StringReader}
import org.htmlcleaner.{PrettyXmlSerializer, HtmlCleaner}
import org.hyperscala.{Container, Markup}
import swing.FileChooser
import java.util.prefs.Preferences
import org.powerscala.IO
import org.jdom2.xpath.XPathFactory
import org.jdom2.filter.Filters
import scala.collection.JavaConversions._
import org.hyperscala.web.{Website, Webpage}
import com.outr.net.http.session.{MapSession, Session}
import com.outr.net.http.request.HttpRequest

/**
 * @author Matt Hicks <matt@outr.com>
 */
object HTMLToScala {
  lazy val WebpageTemplate = IO.copy(getClass.getClassLoader.getResource("webpage.template"))
  lazy val TagTemplate = IO.copy(getClass.getClassLoader.getResource("tag.template"))

  val builder = new SAXBuilder()

  def replaceChildren(parent: HTMLTag with Container[HTMLTag], htmlString: String) = {
    val xml = HTMLToScala.toXML(htmlString, clean = true)
    val body = xml.getChild("body")
    parent.contents.clear()                            // Remove all contents
    parent.read(body)                                  // Read the new data back in
  }

  def toScala[S <: Session](page: Webpage[S], packageName: String, className: String) = {
    val b = new ScalaWebpageBuffer(packageName, className, page)
    b.code
  }

  def toScala(tag: HTMLTag, packageName: String, className: String) = {
    val b = new ScalaTagBuffer(packageName, className, tag)
    b.code
  }

  def toScala(tag: HTMLTag) = {
    val b = new ScalaInstanceBuffer(tag)
    b.code
  }

  /*def toInstantiator[T <: HTMLTag](tag: HTMLTag, className: String) = {
    val source = toScala(tag, null, className)
    val file = File.createTempFile("hyperscala", ".scala")
    try {
      val writer = new FileWriter(file)
      try {
        writer.write(source)
      } finally {
        writer.flush()
        writer.close()
      }
      DynamicCompiler[T](className, file.toURI.toURL)
    } finally {
      file.delete()
    }
  }*/

  /*def toInstantiator[T <: HTMLTag](source: Source, clean: Boolean) = {
    val tag = toHTML(source, clean)
    val className = "Custom%s".format(Unique())
    toInstantiator[T](tag, className)
  }*/

  def toPage[S <: Session](website: Website[S], source: String, clean: Boolean = true)(implicit manifest: Manifest[S]) = {
    val page = new Webpage(website)
    page.html.read(toXML(source, clean))
    page
  }

  def toHTML(source: String, clean: Boolean, rootId: String = null) = {
    val xml = toXML(source, clean)
    val element = if (rootId != null) {
      val query = s"//*[@id='$rootId']"
      val expression = XPathFactory.instance().compile(query, Filters.element())
      expression.evaluate(xml).head
    } else {
      xml
    }
    val root = HTMLTag.create(element.getName)
    root.read(element)
    root
  }

  def toXML(source: String, clean: Boolean) = {
    val content = if (clean) {
      val html = source
      val cleaner = new HtmlCleaner()
      val props = cleaner.getProperties
      val cleaned = cleaner.clean(html)
      new PrettyXmlSerializer(props).getAsString(cleaned)
    } else {
      source
    }
    builder.build(new StringReader(content)).getRootElement
  }

  def main(args: Array[String]): Unit = {
    val website = new Website[MapSession] {
      override protected def createSession(request: HttpRequest, id: String) = new MapSession(id, this)
    }
    Markup.UnsupportedAttributeException = false
    val preferences = Preferences.userNodeForPackage(getClass)
    val filePath = preferences.get("path", ".")
    val chooser = new FileChooser(new File(filePath))
    chooser.title = "Select the HTML file to process"
    chooser.showOpenDialog(null) match {
      case FileChooser.Result.Approve => {
        val file = chooser.selectedFile
        preferences.put("path", file.getParentFile.getAbsolutePath)

        val webpage = toPage(website, IO.copy(file), clean = true)

        val savePath = preferences.get("savePath", preferences.get("path", "."))
        val saver = new FileChooser(new File(savePath))
        saver.title = "Select the location to save the generate code"
        saver.showSaveDialog(null) match {
          case FileChooser.Result.Approve => {
            val file = saver.selectedFile
            preferences.put("savePath", file.getParentFile.getAbsolutePath)

            val scala = toScala(webpage, file.getParentFile.getName, file.getName.substring(0, file.getName.indexOf('.')))
            val writer = new FileWriter(file)
            try {
              writer.write(scala)
            } finally {
              writer.flush()
              writer.close()
            }
          }
          case _ => // Cancelled save
        }
      }
      case _ => // Cancelled open
    }
  }
}