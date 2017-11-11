package moodlehub

import play.api.libs.json.{JsArray, JsObject, JsValue}
import scala.collection.Map

class Section(token: Token, coursePath: Path, obj: JsObject) {
  private val value = obj.value
  private val name = value("name").as[String]
  private val path = coursePath.path + "/" + name
  new java.io.File(path).mkdir()
  private val summary = value("summary").as[String]
  private val modules = value("modules").as[JsArray].value

  private val fileModules: List[Map[String, JsValue]] = modules.filter(_.as[JsObject].value("modplural").as[String] == "Files").map(_.as[JsObject].value).toList

  fileModules.foreach { module =>
    val contents = module("contents").as[JsArray].value
    contents.foreach { file =>
      val fileMap = file.as[JsObject].value
      if(fileMap("type").as[String] == "file") {
        val filename = path + "/" + fileMap("filename").as[String]
        val fileurl = fileMap("fileurl").as[String]
        FileManager.fileDownloader(fileurl, filename)(token)
      }
    }
  }

}

object Section {
  def apply(obj: JsObject)(implicit token: Token, path: Path): Section =
    new Section(token, path, obj)
}
