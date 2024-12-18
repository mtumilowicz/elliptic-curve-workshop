[![Build Status](https://app.travis-ci.com/mtumilowicz/elliptic-curve-workshop.svg?branch=master)](https://app.travis-ci.com/mtumilowicz/elliptic-curve-workshop)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

* references
    * https://habr.com/en/post/692072/
    * https://profilpelajar.com/article/Elliptic_curve_point_multiplication
    * https://en.wikipedia.org/wiki/Projective_plane
    * https://en.wikipedia.org/wiki/Point_at_infinity
    * https://blogs.scientificamerican.com/roots-of-unity/a-few-of-my-favorite-spaces-the-fano-plane/
    * https://www.mathematicalgemstones.com/gemstones/opal/geometry-of-the-real-projective-plane/
    * https://www.amazon.com/Blockchain-Distributed-Ledgers-Alexander-Lipton/dp/9811221510
    * https://crypto.stackexchange.com/questions/70507/in-elliptic-curve-what-does-the-point-at-infinity-look-like
    * https://trustica.cz/2018/03/29/elliptic-curves-point-at-infinity/
    * [Devoxx Greece 2024 - Asymmetric Cryptography: A Deep Dive by Eli Holderness](https://www.youtube.com/watch?v=Q20H-H91-Vk)
    * https://chatgpt.com/

## preface
* goals of this workshop
    * introduction to elliptic curves
        * point addition
        * multiplication by scalar
    * introduction to elliptic curves over finite fields
        * understanding what is the difference to elliptic curves over plane
        * understanding concept of point at infinity
        * understanding why they are useful in cryptography
    * understanding how ECDSA works
* workshop plan
    * implement function to sign a message according to ECDSA spec

## basics
* it could be valuable to take a look here first
    * introduction to cryptography: https://github.com/mtumilowicz/cryptography-math-basics
    * introduction to property based testing: https://github.com/mtumilowicz/scala-zio2-test-aspects-property-based-testing-workshop
* equation for an elliptic curve: y² = x³ + ax + b
    * https://www.desmos.com/calculator/wwpunn6ipg?lang=en
    * called Weierstrass form
    * example: Bitcoin curve
        * y^2 = x^3 + 7
        * p = 2^256 - 2^32 - 2^9 -2^8 - 2^7 - 2^6 - 2^4 - 1
* properties
    * symmetric along the x-axis
        * for any point on the curve A, we can get its mirror point, called -A, by simply mirroring its y coordinate
    * if we draw a line through any of two points not lying on a vertical line, it will intersect the curve
    at exactly one more point
        * reflection of that point is called the sum of A and B
    * if we draw a tangent line through any point A lying on a curve, it will intersect the curve at exactly one point
        * call this point -2A
        * with that we can define multiplication by number
            * A + 2A = 3A
            * example, to get 10A:
              2A = A + A
              4A = 2A + 2A
              8A = 4A + 4A
              10A = 8A + 2A
* summary
    * what we can do
        * addition of two points: (A + B)
        * subtraction of two points: A — B = (A + (-B))
        * multiplication by two: 2A
        * multiplying by any integer: k * Point
    * what we can’t do
        * multiplication of two points
        * division of a point over another point
        * division of a point over a scalar value
            * addition of two points on an elliptic curve (or the addition of one point to itself) yields
            a third point on the elliptic curve whose location has no immediately obvious relationship
            to the locations of the first two
                * repeating this many times over yields a point nP that may be essentially anywhere
            * makes the elliptic curves very good for cryptography
                * reverting this process, i.e., given Q=nP and P, and determining n, can only be done by trying out
                all possible n
                    * an effort that is computationally intractable if n is large
            * analogy: point P on a circle
                 * if you had a point P on a circle, adding 42.57 degrees to its angle may still be a point
                 "not too far" from P, but adding 1000 or 1001 times 42.57 degrees will yield a point that
                 requires a bit more complex calculation to find the original angle

## over finite fields
* elliptic curve: `y² = x³ + ax + b`
* elliptic curve over finite field: `y² mod p = x³ + ax + b mod p`
    * both parts of the equation are now under the modulo p
* to define an elliptic curve, we now need three variables: a, b, and p
    * p is called the order of an elliptic curve
    * example
        * secp256k1 (used in Bitcoin)
            * a=0
            * b=7
            * p=115792089237316195423570985008687907853269984665640564039457584007908834671663
        * visualisation: https://graui.de/code/elliptic2/
            * parameters
                * a = 0
                * b = 7
                * p = 11
* properties
    * has a finite set of points
    * works like an elliptic curve
        * preserves all the properties and formulas of the "original" elliptic curve
    * only difference: slightly modify formulas by executing them mod p
        * multiplicative inverse can be found by the Extended Euclidean algorithm - `O(log(n))`
* order of the point
    * every point on a curve has its own order n
        * example
            * if the order `n` of point `C` is 12, it means that
            * `12C = 0`
            * `13C = C`, `16C = 4C`, `27C = 3C`, etc
* some curves form a single cyclic group
    * others form several non-overlapping cyclic subgroups
        * points on the curve are split into h cyclic subgroups
            * r - "order" of the cyclic subgroup (the total number of all points in the subgroup)
    * number of cyclic subgroups is called "cofactor"
        * if the curve consists of only one cyclic subgroup, its cofactor h = 1
            * example: secp256k1
        * if the curve consists of several subgroups, its cofactor > 1
            * example: Curve25519
                * cofactor = 8
* discrete logarithm problem
    * G generates a subgroup of an EC over a field F, P is another member of EC => find k: P = kG
    * known algorithms have exponential time complexity (n - order of the group)
    * intuition: point multiplication is not a straightforward, linear process
        * defined by a combination of point addition and doubling operations
        * no known efficient way to "reverse" it

### point at infinity
* outline of the problem
    * when trying to sum two points of the elliptic curve which are respective
    negatives, the straight line crossing the two does not intersect the elliptic curve in any other point
        ![alt text](img/negative-point-addition.png)
    * similar problem with doubling top point
        ![alt text](img/doubling-point-problem.png)
    * in both of these cases we say that the resulting point lies at infinity and we typically label it `O`
* point at infinity: `O` is the identity element of elliptic curve arithmetic
* geometric interpretation
    * prerequisite
    * projective plane
        * is a geometric structure that extends the concept of a plane
        * intuition: ordinary plane + "points at infinity" where parallel lines intersect
            * some lines in the plane intersect, but some don’t and it's arbitrary
                * projective planes allow you to smooth out this irritating problem by forcing lines to intersect
            * any two distinct lines in a projective plane intersect at exactly one point
        * anything that satisfies these rules is a projective plane
            * every pair of points is connected by a line
            * every line intersects every other line
            * there are four points such that no line contains more than two of them. (This third condition is not always listed, but it rules out silly cases such as 2 points on one line or several lines that go through one point.)
        * example
            * finite - Fano plane
                ![alt text](img/fano.png)
                * smallest finite projective plane
                * counterintuitive
                    1. it does not have infinitely many points but only 7
                        * lines aren’t made of points, they’re just lines
                    1. one of the lines looks like a circle
                        * lines aren’t made of infinitely many points
                            * the circle is really just a line
            * inifite - Real Projective Plane
                * this is a rather natural model of things we see in reality
                    * suppose you are standing on parallel train tracks and looking out towards the horizon
                        * the tracks are parallel, but they appear to converge to a point on the horizon
                * construction
                    * for each set of parallel lines on the plane attach a point "at infinity" that is connected
                    to all of them
                        * now all the lines intersect
                    * to take care of the requirement that every pair of points is connected by exactly one line
                        * call the set of all the "points at infinity" the "line at infinity"
                * another, more symmetric way to define the real projective plane
                    * think of a point in the real projective plane as a ratio (a:b:c) of three real numbers
                        * called: homogeneous coordinates
                        * we only care about the ratio between the numbers
                            * we use the notation `(X∶Y∶Z)` for a projective point to emphasize this fact
                            * example: (2:4:5) and (6:12:15) describe the same point
                        * line is then defined as the set of solutions `(a:b:c)` to a linear equation `αx+βy+γz=0` for some fixed `α`, `β`, `γ`
                            * equivalent to first definition
                                * each point either has
                                    * `z≠0` => we can normalize so that `z=1` and then take `(x:y:1)` to be the point `(x,y)` in `ℝ2`
                                    * `z=0` => form the new line where `(x:y:0)` corresponds to the direction with "slope" `y/x` (or the vertical direction, if `x` is `0`)
                                        * these are lines going through the origin
                                        * three-dimensional space is reduced to two dimensions by treating lines passing through the origin as a single point
    * projectivization of elliptic curves
        * let `x = X∕Z`, `y = Y∕Z`, assumes the form: `Y2Z = X3 + aXZ2 + bZ3`
            * observation: if `(X,Y,Z)` is a point on the curve, so is `(𝜆X,𝜆Y,𝜆Z)` for any `𝜆`
            * solutions
                * if Z ≠ 0, then we can divide X, Y by Z, consider x = X∕Z, y = Y∕Z and get a solution of the affine equation
                * if Z = 0, then division by Z is not allowed, so that there are points on the projective curve that do not correspond to points on the affine curve
                    * These points have the form (0 ∶ y ∶ 0), for any y ≠ 0.
                    * Given that points in the projective plane are defined as equivalence classes, we can choose the following point O = (0 ∶ 1 ∶ 0), called the point at infinity or the neutral element
                        * intuitively think that O is a point located infinitely high (and low) on the y-axis
        * point at infinity in projective space
            * we have elliptic curve `𝑦2=𝑥3−𝑥+1`
                ![alt text](img/elliptic-curve.png)
            * paste picture above on the plane 𝑧=1
                ![alt text](img/projectivization.png)
                * now we can assign every projective lines `(𝑋:𝑌:𝑍)` satisfying `𝑌2𝑍=𝑋3−𝑋𝑍2+𝑍3` with the affine points `(𝑥,𝑦)` satisfying `𝑦2=𝑥3−𝑥+1` pasted on the plane `𝑧=1`
                    * all except for one such projective line: `(0:1:0)`
                        * which is exactly the point at infinity
                        * explanation
                            * take a first picture and start drawing lines in `R2` between `0.0` and further and further points on EC
                                * you are closer and closer to y axis
                                * and now do the same but start from `0.0.0` and draw in `R3`
                                    * you will get closer and closer to `y` axis as `z` will be smaller and smaller
                                    * in the limit toward infinity, you just get the `𝑦` axis `𝑥=𝑧=0`

## ECDSA (Elliptic Curves Digital Signature Algorithm)
* what we have
    * set of "global" public variables of elliptic curve
        * config (a, b, p)
        * Point G (Generator Point)
            * lies on the curve
            * G can generate any other point in its subgroup by multiplying G by some integer in the range [0...r]
        * order n of point G
            * order of the cyclic subgroup (the total number of all points in the subgroup)
    * PrivateKey
        * any random integer
        * kept in secret by its "owner"
        * deriving public key from private key
            * private key = randomly generated number k
            * public key = private key * G
    * PublicKey
        * just point on the curve
        * there is no way to extract the PrivateKey back
* signing a message
    * what we have
        * PrivateKey
        * message
    * algorithm
        1. generate a random integer k
            * it should be a big number in range `[1, n-1]`
        1. calculate point `R = G * k`
        1. signature: a pair of integers `(r, s)`
            * `r = Rx mod n`
                * (if r == 0 start again with new k)
            * `s = (message + r*PrivateKey)*k^(-1) mod n`
                * k^(-1) is multiplicative inverse of k
* verifying a signature
    * what we have
        * PublicKey
        * message
        * signature `(r, s)`
    * algorithm
        1. calculate U
            * `U = message * s^(-1) mod n`
                * s^(-1) is multiplicative inverse of s
        1. calculate V
            * `V = r*s^(-1) mod n`
                * s^(-1) is multiplicative inverse of s
        1. calculate point C
            * C = U * G + V * PublicKey
        1. if c.x mod n = r => valid
    * proof
        * `C = U * G + V * PublicKey`
        * substitute with definitions
            * `C = message * s^(-1) * G + r * s^(-1) * G * PrivateKey`
            * `C = G * s^(-1) * (message + r * PrivateKey)`
                * from the signing algorithm: `s = (message + r * PrivateKey) * k^(-1)`
                    * `s^(-1) = (message + r * PrivateKey)^(-1) * k`
                    * after substitution: `C = Gk`
                        * thus, if the signature is correct, the x coordinate of C mod n is equal to r
                        (which is, by its definition, the same x coordinate of G * k)
                            * from signing algo: r = Rx mod n, where = Gk
