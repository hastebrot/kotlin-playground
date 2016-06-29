package es4j.example

import com.eventsourcing.Entity
import com.eventsourcing.EntityHandle
import com.eventsourcing.EventStream
import com.eventsourcing.Repository
import com.eventsourcing.StandardCommand
import com.eventsourcing.StandardEvent
import com.eventsourcing.annotations.Index
import com.eventsourcing.index.IndexEngine.IndexFeature.EQ
import com.eventsourcing.index.IndexEngine.IndexFeature.UNIQUE
import com.eventsourcing.index.MemoryIndexEngine
import com.eventsourcing.index.SimpleAttribute
import com.eventsourcing.inmem.MemoryJournal
import com.eventsourcing.repository.EntitySubscriber
import com.googlecode.cqengine.attribute.Attribute
import com.googlecode.cqengine.query.Query
import com.googlecode.cqengine.query.logical.And
import com.googlecode.cqengine.query.logical.Not
import com.googlecode.cqengine.query.logical.Or
import com.googlecode.cqengine.query.option.QueryOptions
import com.googlecode.cqengine.query.simple.Equal
import com.googlecode.cqengine.query.simple.StringEndsWith
import com.googlecode.cqengine.query.simple.StringStartsWith
import com.googlecode.cqengine.resultset.ResultSet
import java.util.UUID
import kotlin.reflect.KClass

//-------------------------------------------------------------------------------------------------
// MAIN METHOD.
//-------------------------------------------------------------------------------------------------

fun main(args: Array<String>) {
    // The repository glues journals, indexing and subscribers together.
    val repository = Repository.create()
    val journal = MemoryJournal()
    val index = MemoryIndexEngine()

    repository.journal = journal
    repository.indexEngine = index
    repository.addEntitySubscriber(object : EntitySubscriber<Entity<*>> {
        override fun onEntity(entity: EntityHandle<Entity<*>>) {
            println(entity.get())
        }
    })
    repository.startAsync().awaitRunning()

    // Hybrid Logical Clocks - timestamps every command and event.
    // LMAX Disruptor - processes commands.
    // CQEngine - indexes commands and events.

    val createUser = CreateUser()
    val user = repository.publish(createUser).get()
    println(user)

//    repository.stopAsync()
}

//-------------------------------------------------------------------------------------------------
// MODELS.
//-------------------------------------------------------------------------------------------------

data class User(override val repository: Repository,
                override val id: UUID) : Model {
    companion object {
        fun lookup(repository: Repository,
                   id: UUID): User? {
            val result = repository.query(UserCreated::class) {
                (UserCreated.ID equalTo id) and (UserCreated.NAME startsWith "user")
            }
            return result.use {
                val userCreated = it.uniqueResult()
                User(repository, userCreated.uuid())
            }
        }
    }
}

//-------------------------------------------------------------------------------------------------
// COMMANDS.
//-------------------------------------------------------------------------------------------------

class CreateUser() : StandardCommand<User, UserCreated>() {
    override fun events(repository: Repository): EventStream<UserCreated> {
        val userCreated = UserCreated()
        return EventStream.ofWithState(userCreated, userCreated)
    }

    override fun onCompletion(userCreated: UserCreated,
                              repository: Repository): User? {
        return User.lookup(repository, userCreated.uuid())
    }

    override fun toString() = "${javaClass.simpleName}(${uuid()})"
}

//-------------------------------------------------------------------------------------------------
// EVENTS.
//-------------------------------------------------------------------------------------------------

class UserCreated : StandardEvent() {
    override fun toString() = "${javaClass.simpleName}(${uuid()})"

    companion object {
        @Index(EQ, UNIQUE)
        val ID = simpleAttribute<UserCreated, UUID>("id") { it.uuid() }

        @Index(EQ, UNIQUE)
        val NAME = simpleAttribute<UserCreated, String>("name") { "user" }
    }
}

//-------------------------------------------------------------------------------------------------
// HELPERS.
//-------------------------------------------------------------------------------------------------

interface Model {
    val repository: Repository
    val id: UUID
}

fun <E : Entity<*>> Repository.query(kClass: KClass<E>,
                                     builder: QueryBuilder.() -> Query<EntityHandle<E>>):
        ResultSet<EntityHandle<E>> {
    return query(kClass.java, builder(QueryBuilder()))
}

class QueryBuilder {
    fun <O> and(vararg queries: Query<O>) = And(queries.toList())
    fun <O> or(vararg queries: Query<O>) = Or(queries.toList())
    fun <O> not(query: Query<O>) = Not(query)

    infix fun <O> Query<O>.and(query: Query<O>) = And(listOf(this, query))
    infix fun <O> Query<O>.or(query: Query<O>) = Or(listOf(this, query))

    infix fun <O, A> Attribute<O, A>.equalTo(value: A) =
        Equal(this, value)
    infix fun <O, A : CharSequence> Attribute<O, A>.startsWith(value: A) =
        StringStartsWith(this, value)
    infix fun <O, A : CharSequence> Attribute<O, A>.endsWith(value: A) =
        StringEndsWith(this, value)
}

inline fun <O : Entity<*>, A> simpleAttribute(name: String,
                                              crossinline action: (O) -> A) =
    object : SimpleAttribute<O, A>(name) {
        override fun getValue(`object`: O,
                              queryOptions: QueryOptions): A = action(`object`)
    }
