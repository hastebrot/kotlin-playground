package requery.person

import com.zaxxer.hikari.HikariDataSource
import io.requery.Persistable
import io.requery.cache.EntityCacheBuilder
import io.requery.meta.EntityModel
import io.requery.sql.BoundParameters
import io.requery.sql.Configuration
import io.requery.sql.ConfigurationBuilder
import io.requery.sql.EntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.StatementListener
import io.requery.sql.TableCreationMode
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.layout.PatternLayout
import requery.person.personEntity.AddressEntity
import requery.person.personEntity.Models
import requery.person.personEntity.PersonEntity
import rx.schedulers.Schedulers
import java.sql.Statement
import javax.cache.Caching
import javax.sql.DataSource

fun main(args: Array<String>) {
    configureRootLogger(Level.OFF)

    val dataSource = buildDataSource()
    val entityModel = Models.PERSONENTITY

    val dataStoreConfig = buildDataStoreConfig(dataSource, entityModel)
    val dataStore = EntityDataStore<Persistable>(dataStoreConfig)

    createTables(dataStoreConfig)
    runSamples(dataStore)

    dataStore.close()
}

private fun runSamples(dataStore: EntityDataStore<Persistable>) {
    Models.PERSONENTITY
        .typeOf(PersonEntity::class.java)
        .attributes
        .associateBy { it.name }
        .let { println(it) }

    dataStore.insert(PersonEntity().apply {
        age = 30
        address = AddressEntity().apply {
            city = "hamburg"
        }
    })
    dataStore.insert(PersonEntity().apply {
        age = 31
    })

    val scalar = dataStore.count(PersonEntity::class.java)
        .get()
    scalar.value().let { println(it) }

    val result = dataStore.select(PersonEntity::class.java)
        .limit(10)
        .get()
    result.forEach { println(it) }

    val countScalar = dataStore.count(PersonEntity::class.java)
        .get()
    countScalar.toSingle()
        .subscribe { println(it) }

    val selectResult = dataStore.select(PersonEntity::class.java)
        .get()
    selectResult.toObservable()
        .subscribeOn(Schedulers.io())
        .toBlocking()
        .subscribe { println(it) }

    val selectResult2 = dataStore.select(PersonEntity::class.java)
        .get()
    selectResult2.toObservable()
        .subscribe {
            println(PersonEntity.AGE.property[it])
        }
}

private fun buildDataStoreConfig(dataSource: DataSource,
                                 entityModel: EntityModel): Configuration {
    val cacheManager = Caching.getCachingProvider().cacheManager
    val configuration = ConfigurationBuilder(dataSource, entityModel)
//        .useDefaultLogging()
        .addStatementListener(sqlStatementListener)
        .setEntityCache(EntityCacheBuilder(entityModel)
            .useReferenceCache(true)
            .useSerializableCache(true)
            .useCacheManager(cacheManager)
            .build())
        .build()
    return configuration
}

private fun createTables(configuration: Configuration) {
    val schemaModifier = SchemaModifier(configuration)
    schemaModifier.createTables(TableCreationMode.DROP_CREATE)
}

private fun buildDataSource(): DataSource {
    val dataSource = HikariDataSource()
    dataSource.jdbcUrl = "jdbc:h2:mem:samples;DB_CLOSE_DELAY=-1"
    dataSource.username = "sa"
    return dataSource
}

private fun configureRootLogger(loggerLevel: Level) {
    val loggerContext = LogManager.getContext(false) as LoggerContext
    val loggerName = LogManager.ROOT_LOGGER_NAME

    val consoleAppender = ConsoleAppender
        .createDefaultAppenderForLayout(PatternLayout.createDefaultLayout())
        .apply { start() }

    loggerContext.configuration
        .getLoggerConfig(loggerName)
        .apply {
            level = loggerLevel
            addAppender(consoleAppender, null, null)
        }
}

private val sqlStatementListener = object : StatementListener {
    override fun beforeExecuteQuery(statement: Statement,
                                    sql: String,
                                    parameters: BoundParameters?) {
        println(sql)
    }

    override fun afterExecuteQuery(statement: Statement) { }

    override fun beforeExecuteUpdate(statement: Statement,
                                     sql: String,
                                     parameters: BoundParameters?) {
        println(sql)
    }

    override fun afterExecuteUpdate(statement: Statement) { }
}
