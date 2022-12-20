package ec

import generator.EcPointGenerator
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue, check}

object EllipticCurvePointSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("")(
    test("a") {
      check(EcPointGenerator.secp256k1) { point =>
        assertTrue(point.add(point) == point.multiply(2))
      }
    }
  )
}