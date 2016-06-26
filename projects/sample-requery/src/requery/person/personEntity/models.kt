package requery.person.personEntity

import io.requery.Persistable
import io.requery.query.MutableResult
import java.io.Serializable
import java.net.URL
import java.util.Date
import java.util.Optional
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.Transient
import javax.persistence.Version

@Entity
interface Address : Serializable {
    @get:Id
    @get:GeneratedValue
    var id: Int

    var line1: String
    var line2: String
    var state: String

    @get:Embedded
    val coordinate: Coordinate

    @get:Column(length = 5)
    var zip: String

    @get:Column(length = 2)
    var country: String

    var city: String

    @get:OneToOne(mappedBy = "address")
    var person: Person

    var type: AddressType
}

enum class AddressType {
    HOME,
    WORK,
}

@Embeddable
interface Coordinate {
    @get:Column(nullable = false)
    var latitude: Float

    @get:Column(nullable = false)
    var longitude: Float
}

@Entity
@Table(name = "Groups")
interface Group : Serializable, Persistable {
    @get:Id
    @get:GeneratedValue
    var id: Int

    var name: String
    val description: Optional<String>

    var type: GroupType
    var picture: Array<Byte>

    @get:Version
    var version: Int

    @get:JoinTable(
        joinColumns = arrayOf(JoinColumn(name = "personId", referencedColumnName = "id")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "groupId", referencedColumnName = "id"))
    )
    @get:ManyToMany
    var persons: MutableResult<Person>

    @get:Transient
    var temporaryName: String
}

enum class GroupType {
    PUBLIC,
    PRIVATE,
}

@Entity
interface Person : Serializable {
    @get:Id
    @get:GeneratedValue
    var id: Int

    var name: String
    var email: String
    var birthday: Date

    @get:Column(nullable = true)
    var age: Int

    @get:OneToOne
    @get:JoinColumn(foreignKey = ForeignKey())
    var address: Address

    @get:OneToMany(
        mappedBy = "owner",
        cascade = arrayOf(CascadeType.REMOVE, CascadeType.PERSIST)
    )
    var phoneNumbers: MutableResult<Phone>

    @get:OneToMany(mappedBy = "owner")
    var phoneNumbersSet: MutableSet<Phone>

    @get:OneToMany
    var phoneNumbersList: MutableList<Phone>

    @get:ManyToMany(mappedBy = "persons")
    var groups: MutableResult<Group>

    var about: String

    @get:Column(unique = true)
    var uuid: UUID

    var homepage: URL
}

@Entity
interface Phone : Serializable {
    @get:Id
    @get:GeneratedValue
    var id: Int

    var phoneNumber: String
    var normalized: Boolean

    @get:ManyToOne
    var owner: Person
}
