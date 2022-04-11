package tk.kvakva.shownicehashstat

import android.util.Log
//import org.apache.commons.codec.binary.Hex
import com.google.crypto.tink.subtle.Hex
import org.junit.Test

import org.junit.Assert.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun time_time() {
        val t = Instant.parse("2020-03-02T08:00:00Z").atZone(ZoneId.systemDefault())
        println(t)
        println(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm.ss").format(t))
    }



    private fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }

    val accessKey = "4ebd366d-76f4-4400-a3b6-e51515d054d6"
    val secret = "fd8a1652-728b-42fe-82b8-f623e56da8850750f5bf-ce66-4ca7-8b84-93651abc723b"
    val orgId = "da41b3bc-3d0b-4226-b7ea-aee73f94a518"
    val nonce = "9675d0f8-1325-484b-9594-c9d6d3268890"
    val serverTime = "1543597115712"
    val accessType = "GET"
    val accessPath = "/main/api/v2/hashpower/orderBook"
    val query = "algorithm=X16R&page=0&size=100"

    private fun signInputByHmacSha256(segments: List<ByteArray?>): String? {
        return try {
            val sha256_HMAC: Mac = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")

            sha256_HMAC.init(secretKey)

            segments.forEach { b ->

                println(String(b?:"null".toByteArray()))
            }
            println("---------")
            var first = true
            for (segment in segments) {
                if (!first) {
                    sha256_HMAC.update(0.toByte())
                } else {
                    first = false
                }
                if (segment != null) {
                    sha256_HMAC.update(segment)
                    println(String(segment))
                }
            }
            println("----------")
            sha256_HMAC.doFinal().toHex()

        } catch (e: Exception) {
            throw RuntimeException("Cannot create HmacSHA256", e)
        }
    }

    @Test
    fun hnha() {



        val input =
            listOf(secret, accessKey, serverTime, nonce, null, orgId, null, accessType, accessPath)
                .map { it?.toByteArray(Charsets.ISO_8859_1) }
        val testInput = listOf(
            accessKey,
            serverTime,
            nonce,
            null,
            orgId,
            null,
            accessType,
            accessPath,
            query
        )
            .map { it?.toByteArray(Charsets.ISO_8859_1) }

        val digest = signInputByHmacSha256(testInput)
        val auth = "$accessKey:$digest"
        println( "hnha: $auth", )

        val inputstring =
            accessKey.toByteArray(Charsets.ISO_8859_1).plus(0.toByte())
                .plus(serverTime.toByteArray(Charsets.ISO_8859_1)).plus(0.toByte())
                .plus(nonce.toByteArray(Charsets.ISO_8859_1)).plus(0.toByte()).plus(0.toByte())
                .plus(orgId.toByteArray(Charsets.ISO_8859_1)).plus(0.toByte())
                .plus(0.toByte()).plus(accessType.toByteArray(Charsets.ISO_8859_1)).plus(0.toByte())
                .plus(accessPath.toByteArray(Charsets.ISO_8859_1)).plus(0.toByte())
                .plus(query.toByteArray(Charsets.ISO_8859_1))
                //.plus(0.toByte())
                //.plus("extendedResponse=true".toByteArray(Charsets.ISO_8859_1))

        val apisecret =
            SecretKeySpec(secret.toByteArray(Charsets.ISO_8859_1), "HmacSHA256")
        val nhsha265HMAC = Mac.getInstance("HmacSHA256")
        nhsha265HMAC.init(apisecret)
        val hash = Hex.encode(nhsha265HMAC.doFinal(inputstring))
        println("mnha: $accessKey:$hash")
    }

}

