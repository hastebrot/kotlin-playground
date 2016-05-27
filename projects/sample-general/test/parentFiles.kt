import com.winterbe.expekt.expect
import java.io.File

fun main(args: Array<String>) {
    val currentDir = File(".")
    val parentDirs = generateSequence(currentDir.canonicalFile) { it.parentFile }
    val wrapperDir = parentDirs.map { File(it, "gradle/wrapper") }.find { it.isDirectory }

    expect(wrapperDir).satisfy { it.path.endsWith("gradle/wrapper") }
}
