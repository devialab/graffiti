package graffiti.oauth

import spray.http.HttpHeaders._
import spray.http._
import spray.routing.RequestContext
import spray.routing.authentication.HttpAuthenticator

import scala.concurrent.{ExecutionContext, Future}

/**
 * @author Alexander De Leon <me@alexdeleon.name>
 */
class OauthAuthenticator[T](tokenAuthenticator: TokenAuthenticator[T], realm: String = "Oauth")
                           (implicit val executionContext: ExecutionContext) extends HttpAuthenticator[T] {

  override def authenticate(credentials: Option[HttpCredentials], ctx: RequestContext): Future[Option[T]] =
    tokenAuthenticator {
      credentials.flatMap {
        case OAuth2BearerToken(token) => Some(token)
        case _  => None
      }
    }

  override def getChallengeHeaders(httpRequest: HttpRequest): List[HttpHeader] =
    `WWW-Authenticate`(HttpChallenge(scheme = "Bearer", realm = realm, params = Map.empty)) :: Nil
}

object OAuth {
  def apply[T](tokenAuthenticator: TokenAuthenticator[T], realm: String = "Oauth")(implicit executionContext: ExecutionContext)
    =  new OauthAuthenticator[T](tokenAuthenticator, realm)
}