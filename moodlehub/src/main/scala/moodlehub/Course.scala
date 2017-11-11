package moodlehub

import play.api.libs.json.{JsArray, JsObject, JsValue}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits._

class Course(token: Token, path: Path, name: String, courseId: Int) {

  var sections: Array[Section] = _

  Client.getContents(courseId)(token).onComplete {
    case Success(s) => sections = s.as[Array[JsValue]].map{ section =>
      Section(section.as[JsObject])(path)
    }
    case Failure(e) => throw e
  }

}

object Course {
  def apply(name: String, courseId: Int)(implicit token: Token, path: Path): Course =
    new Course(token, Path((path.path + name + "/").replace(' ', '_')), name, courseId)
}
