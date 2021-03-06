package net.sc8s.schevo

import net.sc8s.schevo.SchevoSpec.{Full, Minimal}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SchevoSpec extends AnyWordSpecLike with Matchers {
  "Schevo" should {
    "evolve case class with minimal overrides" in {
      import Minimal._
      val itemV1 = ItemV1("first", "last")

      val migrated = itemV1.evolve

      migrated shouldBe a[LatestT]
      migrated shouldBe ItemV3("first last", enabled = true)
      migrated.caseClass shouldBe a[ItemV3]
      // this just shows how you could obtain the latest trait when using e.g. circe
    }
    "evolve case class with full overrides" in {
      import Full._
      val itemV1 = ItemV1("first", "last")

      val evolved = itemV1.evolve

      evolved shouldBe
        // no need to reference actual latest case class
        Full.apply("first last", true)

      // this just shows how you could obtain the latest trait when using e.g. circe
      evolved.caseClass shouldBe Full.apply("first last", true)

      // common ancestor
      evolved shouldBe a[SomeOtherBaseClassHigherUp]
    }
    "evolve using base trait" in {
      import Minimal._
      val itemV1 = ItemV1("first", "last")

      Seq(itemV1: Any).collect {
        case item: Schevo.VersionBase[_] => item.evolve
      } shouldBe Seq(itemV1.evolve)
    }
  }
}

object SchevoSpec {
  object Minimal extends Schevo {
    override type LatestCaseClass = ItemV3

    case class ItemV3(name: String, enabled: Boolean) extends LatestT {
      override def caseClass = this
    }

    case class ItemV2(name: String) extends VersionT {
      override def evolve = ItemV3(name, enabled = true)
    }

    case class ItemV1(firstName: String, lastName: String) extends VersionT {
      override def evolve = ItemV2(s"$firstName $lastName").evolve
    }
  }

  object Full extends Schevo {
    sealed trait SomeOtherBaseClassHigherUp

    def apply = ItemV3.apply _

    trait Latest extends LatestT {
      val name: String
      val enabled: Boolean

      override def evolve = this
    }

    override type LatestCaseClass = ItemV3

    case class ItemV3(name: String, enabled: Boolean) extends Latest with Version {
      override def caseClass = this
    }

    trait Version extends VersionT with SomeOtherBaseClassHigherUp

    case class ItemV2(name: String) extends Version {
      override def evolve = ItemV3(name, enabled = true)
    }

    case class ItemV1(firstName: String, lastName: String) extends Version {
      override def evolve = ItemV2(s"$firstName $lastName").evolve
    }
  }
}
