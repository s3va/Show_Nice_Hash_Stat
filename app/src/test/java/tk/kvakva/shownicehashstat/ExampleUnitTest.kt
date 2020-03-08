package tk.kvakva.shownicehashstat

import com.google.crypto.tink.subtle.Hex
import org.junit.Test

import org.junit.Assert.*
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

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
    fun randstringxxx() {
        val v =
            UUID.nameUUIDFromBytes("jyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyjjjjjjj".toByteArray())
                .toString()
        val s = UUID.randomUUID().toString() //.substring(0,36)
        println("$s length: ${s.length}")
        println("$v length: ${v.length}")
    }

    @Test
    fun hmacaa() {
        val xTime = "1543597115712"
        val xNonce = "9675d0f8-1325-484b-9594-c9d6d3268890"
        val xOrganizationId = "da41b3bc-3d0b-4226-b7ea-aee73f94a518"
        val xRequestId = UUID.randomUUID().toString()
        //val xAuth = nhApiKey.value // + hmac signature
        val xApiKey = "4ebd366d-76f4-4400-a3b6-e51515d054d6"

        val urlPath = "/main/api/v2/hashpower/orderBook"

        val inputstring = xApiKey.toByteArray(Charsets.ISO_8859_1)?.plus(0.toByte())
            ?.plus(xTime.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
            ?.plus(xNonce.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())?.plus(0.toByte())
            ?.plus(xOrganizationId.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())?.plus(0.toByte())
            ?.plus("GET".toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
            ?.plus(urlPath.toByteArray(Charsets.ISO_8859_1))?.plus(0.toByte())
            ?.plus("algorithm=X16R&page=0&size=100".toByteArray(Charsets.ISO_8859_1))

        val sha265_HMAC = Mac.getInstance("HmacSHA256")
        val apisecret =
            SecretKeySpec("fd8a1652-728b-42fe-82b8-f623e56da8850750f5bf-ce66-4ca7-8b84-93651abc723b".toByteArray(Charsets.ISO_8859_1), "HmacSHA256")
        sha265_HMAC.init(apisecret)
        val hash = Hex.encode(sha265_HMAC.doFinal(inputstring))

        var nhMiningINfo: MiningInfo
        var headerMap = mapOf<String, String>(
            "X-Time" to xTime,
            "X-Nonce" to xNonce,
            "X-Organization-Id" to xOrganizationId,
            "X-Request-Id" to xRequestId,
            "X-Auth" to xApiKey + ":" + hash
        )

        println(headerMap)

    }


}
