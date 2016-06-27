package es4j.example

//compile "com.eventsourcing:eventsourcing-core:0.4.0-SNAPSHOT"
//compile "com.eventsourcing:eventsourcing-inmem:0.4.0-SNAPSHOT"
//compile "com.eventsourcing:eventsourcing-h2:0.4.0-SNAPSHOT"

import com.eventsourcing.Entity
import com.eventsourcing.EntityHandle
import com.eventsourcing.Event
import com.eventsourcing.Model
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
import com.googlecode.cqengine.query.QueryFactory.equal
import com.googlecode.cqengine.query.option.QueryOptions
import java.util.UUID
import java.util.stream.Collectors
import java.util.stream.Stream

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
    // LMAX Disruptor - process commands
    // CQEngine - command and event indices

    val createUser = CreateUser0()
    val user = repository.publish(createUser).get()
    println(user)
}

//-------------------------------------------------------------------------------------------------
// MODELS.
//-------------------------------------------------------------------------------------------------

data class User(private val repository: Repository,
                private val id: UUID) : Model {
    override fun getRepository() = repository
    override fun id() = id

    companion object {
        fun lookup(repository: Repository,
                   id: UUID): User? {
            repository.query(UserCreated::class.java, equal(UserCreated.ID, id)).use {
                return User(repository, it.uniqueResult().uuid())
            }
        }
    }
}

//-------------------------------------------------------------------------------------------------
// COMMANDS.
//-------------------------------------------------------------------------------------------------

class CreateUser() : StandardCommand<User>() {
    lateinit private var repository: Repository
    lateinit private var id: UUID

    override fun events(repository: Repository): Stream<Event> {
        this.repository = repository
        return Stream.of(UserCreated().apply { id = uuid() })
    }

    override fun onCompletion(): User? {
        return User.lookup(repository, id)
    }

    override fun toString() = "${javaClass.simpleName}(${uuid()})"
}

class CreateUser0() : StandardCommand0<User, Event>() {
    override fun events0(repository: Repository): Stream<Event> {
        return Stream.of(UserCreated())
    }

    override fun onComplete0(repository: Repository,
                            event: Event): User? {
        return User.lookup(repository, event.uuid())
    }

    override fun toString() = "${javaClass.simpleName}(${uuid()})"
}

abstract class StandardCommand0<R, E: Event> : StandardCommand<R>() {
    lateinit private var lastRepository: Repository
    lateinit private var lastEvent: E

    override fun events(repository: Repository): Stream<E> {
        val stream = events0(repository)
        val streamList = stream.collect(Collectors.toList<E>())
        lastRepository = repository
        lastEvent = streamList.last()
        val streamBuilder = Stream.builder<E>()
        streamList.forEach { streamBuilder.add(it) }
        return streamBuilder.build()
    }
    override fun onCompletion(): R? = onComplete0(lastRepository, lastEvent)

    abstract fun events0(repository: Repository): Stream<E>
    abstract fun onComplete0(repository: Repository, event: E): R?
}

//-------------------------------------------------------------------------------------------------
// EVENTS.
//-------------------------------------------------------------------------------------------------

class UserCreated : StandardEvent() {
    override fun toString() = "${javaClass.simpleName}(${uuid()})"

    companion object {
        @Index(EQ, UNIQUE)
        val ID = object : SimpleAttribute<UserCreated, UUID>("id") {
            override fun getValue(userCreated: UserCreated,
                                  queryOptions: QueryOptions): UUID {
                return userCreated.uuid()
            }
        }
    }
}

