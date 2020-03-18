package tk.kvakva.shownicehashstat

import org.junit.Test

import org.junit.Assert.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


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
}

