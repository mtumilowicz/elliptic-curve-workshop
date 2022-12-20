package ecdsa

import ec._
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

import scala.util.Random

object EcdsaSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("")(
    test("") {
      val x = Curve.Secp256k1.x
      val y = Curve.Secp256k1.y
      val n = Curve.Secp256k1.n
      val g_point = Curve.Secp256k1.g_point

      def signMessage(message: BigInt, privateKey: BigInt): (BigInt, BigInt) = {
        val k = BigInt(n.bitLength, new Random())
        val r_point = g_point.multiply(k)
        val r = r_point.x.mod(n)
        if (r == 0) signMessage(message, privateKey)
        val k_inverse = k.modInverse(n)
        val s = (k_inverse * (message + r * privateKey)).mod(n)
        (r, s)
      }

      def verify_signature(signature: (BigInt, BigInt), message: BigInt, public_key: EllipticCurvePoint): Boolean = {
        val (r, s) = signature
        val s_inverse = s.modInverse(n)
        val u = (message * s_inverse).mod(n)
        val v = (r * s_inverse).mod(n)
        val c_point = g_point.multiply(u).add(public_key.multiply(v))
        c_point.x == r
      }

      val private_key = 123456789012345L
      val public_key = g_point.multiply(private_key)
      val message = 12345

      val signature = signMessage(message, private_key)

      assertTrue(verify_signature(signature, message, public_key))
    }
  )
}
