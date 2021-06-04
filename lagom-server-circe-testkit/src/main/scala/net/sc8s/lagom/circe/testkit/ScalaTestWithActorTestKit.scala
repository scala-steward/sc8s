package net.sc8s.lagom.circe.testkit

import akka.actor.testkit.typed.scaladsl.ActorTestKit.ApplicationTestConfig
import akka.actor.typed.scaladsl.adapter.ClassicActorSystemOps
import akka.persistence.testkit.{PersistenceTestKitPlugin, PersistenceTestKitSnapshotPlugin}
import com.typesafe.config.{Config, ConfigFactory}
import net.sc8s.akka.circe.CirceSerializerRegistry
import net.sc8s.lagom.circe.ActorSystemProvider
import play.api.Environment

abstract class ScalaTestWithActorTestKit(
                                          circeSerializerRegistry: CirceSerializerRegistry,
                                          additionalConfig: Config = ConfigFactory.empty()
                                        ) extends akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit(ActorSystemProvider.start(
  additionalConfig
    .withFallback(PersistenceTestKitPlugin.config)
    .withFallback(PersistenceTestKitSnapshotPlugin.config)
    .withFallback(ConfigFactory.load())
    .withFallback(ApplicationTestConfig),
  Environment.simple(),
  circeSerializerRegistry
).toTyped)
