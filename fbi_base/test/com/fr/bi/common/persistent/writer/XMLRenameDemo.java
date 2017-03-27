package com.fr.bi.common.persistent.writer;

import com.fr.bi.common.world.people.Person;
import com.fr.bi.common.world.people.PersonOld1;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import junit.framework.TestCase;

/**
 * Created by neil on 17-3-26.
 */
public class XMLRenameDemo extends TestCase{

    public void testDemoRename() throws Exception{
        writeObjectToXml();
        renameClassAndFieldName();
        Object o = readNewObjectFromXml();

        assertTrue(o instanceof Person);
        assertTrue(PersonOld1.getChenHe().getNameOld().equals(((Person)o).getName()));
        assertTrue(PersonOld1.getChenHe().getAge() == ((Person)o).getAge());
        assertTrue(PersonOld1.getChenHe().getMale() == ((Person)o).getMale());
    }

    private void writeObjectToXml() {
        XMLWriterTest.generate(PersonOld1.getChenHe(), "PersonRenameDemo");
    }

    private void renameClassAndFieldName() {
        //Do nothing
        //这里建了新的Person 并且字段nameOld改成了name
    }

    private Object readNewObjectFromXml() throws Exception{
        Class objClass = Class.forName(Person.class.getName());
        Object o;
        o = BIConstructorUtils.forceConstructObject(objClass);
        o = XMLWriterTest.get(o, "PersonRenameDemo");
        return o;
    }

}
