package es4j.example

//compile "com.eventsourcing:eventsourcing-core:0.4.0-SNAPSHOT"
//compile "com.eventsourcing:eventsourcing-inmem:0.4.0-SNAPSHOT"
//compile "com.eventsourcing:eventsourcing-h2:0.4.0-SNAPSHOT"

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
import com.googlecode.cqengine.query.QueryFactory.equal
import com.googlecode.cqengine.query.option.QueryOptions
import java.util.UUID
import java.util.stream.Stream

//-------------------------------------------------------------------------------------------------
// MAIN METHOD.
//-------------------------------------------------------------------------------------------------

fun main(args: Array<String>) {
    val repository = Repository.create()
    val journal = MemoryJournal()
    val index = MemoryIndexEngine()

    repository.journal = journal
    repository.indexEngine = index
//    repository.physicalTimeProvider = NTPServerTimeProvider()
    repository.startAsync().awaitRunning()

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
}

//-------------------------------------------------------------------------------------------------
// EVENTS.
//-------------------------------------------------------------------------------------------------

class UserCreated : StandardEvent() {
    companion object {
        @Index(EQ, UNIQUE)
        val ID = object : SimpleAttribute<UserCreated, UUID>("id") { // issue: type inference.
            override fun getValue(userCreated: UserCreated,
                                  queryOptions: QueryOptions): UUID {
                return userCreated.uuid()
            }
        }
    }
}
