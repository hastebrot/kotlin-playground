// Generated file do not edit, generated by io.requery.processor.EntityProcessor
package requery.person.personEntity;

import io.requery.meta.EntityModel;
import io.requery.meta.EntityModelBuilder;
import javax.annotation.Generated;

@Generated("io.requery.processor.EntityProcessor")
public class Models {
    public static final EntityModel PERSONENTITY = new EntityModelBuilder("personEntity")
    .addType(AddressEntity.$TYPE)
    .addType(GroupEntity.$TYPE)
    .addType(PhoneEntity.$TYPE)
    .addType(GroupEntity_PersonEntity.$TYPE)
    .addType(PersonEntity.$TYPE)
    .build();

    private Models() {
    }
}
