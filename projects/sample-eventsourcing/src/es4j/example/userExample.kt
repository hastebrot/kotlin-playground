package es4j.example

//compile "com.eventsourcing:eventsourcing-core:0.4.0-SNAPSHOT"
//compile "com.eventsourcing:eventsourcing-inmem:0.4.0-SNAPSHOT"
//compile "com.eventsourcing:eventsourcing-h2:0.4.0-SNAPSHOT"

import com.eventsourcing.Entity
import com.eventsourcing.EntityHandle
import com.eventsourcing.EventStream
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

    val createUser = CreateUser()
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

class CreateUser() : StandardCommand<User, UserCreated>() {
    override fun events(repository: Repository): EventStream<UserCreated> {
        val state = UserCreated()
        return EventStream.ofWithState(state, state)
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
        val ID = object : SimpleAttribute<UserCreated, UUID>("id") {
            override fun getValue(userCreated: UserCreated,
                                  queryOptions: QueryOptions): UUID {
                return userCreated.uuid()
            }
        }
    }
}

