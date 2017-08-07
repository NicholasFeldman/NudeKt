import org.junit.Test
import tech.feldman.nudekt.isNude
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Test {

    val notNude1 = javaClass.classLoader.getResource("notNude1.jpg").file
    val notNude2 = javaClass.classLoader.getResource("notNude2.jpg").file
    val notNude3 = javaClass.classLoader.getResource("notNude3.jpg").file

    val nude1 = javaClass.classLoader.getResource("nude1.jpg").file

    @Test fun notNude() {
        assertFalse(isNude(notNude1))
        assertFalse(isNude(notNude2))
        assertFalse(isNude(notNude3))
    }

    @Test fun isNude() {
        assertTrue(isNude(nude1))
    }
}