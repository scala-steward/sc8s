package net.sc8s.elastic.lagom

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import cats.implicits.catsStdInstancesForFuture
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.akka.{AkkaHttpClient, AkkaHttpClientSettings}
import com.softwaremill.macwire.wire
import com.typesafe.config.Config
import net.sc8s.elastic.{Evolver, Index, IndexSetup}

import scala.concurrent.Future

trait ElasticComponents {
  val actorSystem: ActorSystem

  val elasticIndices: Set[Index]

  def config: Config

  implicit val indexSetup: IndexSetup = IndexSetup(elasticClient, actorSystem.toTyped)

  lazy implicit val elasticClient: ElasticClient[Future] = {
    import actorSystem.dispatcher
    ElasticClient(AkkaHttpClient(AkkaHttpClientSettings())(actorSystem))
  }

  lazy val evolver: Evolver.Wiring = Evolver.init(wire[Evolver.Component])(actorSystem.toTyped)
}

