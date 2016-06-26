package ebean

import com.avaje.ebean.EbeanServerFactory
import com.avaje.ebean.config.ServerConfig
import com.zaxxer.hikari.HikariDataSource
import javax.persistence.Entity
import javax.persistence.Id

@Entity
open class Person {
    @Id open var id: Long? = null

    open var firstName: String? = null
    open var lastName: String? = null
}

fun main(args: Array<String>) {
    val dataSource = HikariDataSource()
    dataSource.jdbcUrl = "jdbc:h2:mem:samples;DB_CLOSE_DELAY=-1"
    dataSource.username = "sa"

    val serverConfig = ServerConfig()
    serverConfig.name = "samples"
    serverConfig.dataSource = dataSource

    serverConfig.classes = listOf(Person::class.java)

    serverConfig.isDefaultServer = false
    serverConfig.isRegister = false

    serverConfig.isDdlGenerate = true
    serverConfig.isDdlRun = true

    val ebeanServer = EbeanServerFactory.create(serverConfig)
    println(ebeanServer.name)
}

