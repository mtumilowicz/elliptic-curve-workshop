package ecdsa

import ec._
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

import scala.util.Random

object EcdsaSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("")(
    test("") {
      val secp256k1CurveConfig = EllipticCurveConfig(0, 7, BigInt("115792089237316195423570985008687907853269984665640564039457584007908834671663"))

      val x = BigInt("55066263022277343669578718895168534326250603453777594175500187360389116729240")
      val y = BigInt("32670510020758816978083085130507043184471273380659243275938904335757337482424")
      val n = BigInt("115792089237316195423570985008687907852837564279074904382605163141518161494337")
      val g_point = EllipticCurvePoint(x, y, secp256k1CurveConfig)

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
