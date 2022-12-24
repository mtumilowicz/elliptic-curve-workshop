package ecdsa

import ec._
import zio.Scope
import zio.test.{Gen, Spec, TestEnvironment, ZIOSpecDefault, assertTrue, check}

import scala.util.Random

object EcdsaSpec extends ZIOSpecDefault {

  val curve = Curve.Secp256k1
  val notary = Notary(curve)
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("")(
    test("") {
      check(Gen.int(1, Int.MaxValue), Gen.int(1, Int.MaxValue)) { (message, private_key) =>
        val g = curve.g
        val public_key = g.multiply(private_key)

        val signature = notary.signMessage(message, private_key)

        assertTrue(notary.verify_signature(signature, message, public_key))
      }
    }
  )
}
