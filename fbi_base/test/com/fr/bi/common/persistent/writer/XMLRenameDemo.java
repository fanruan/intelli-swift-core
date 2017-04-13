package com.fr.bi.common.persistent.writer;

import com.fr.bi.common.world.people.Person;
import com.fr.bi.common.world.people.PersonOld1;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neil on 17-3-26.
 */
public class XMLRenameDemo extends TestCase{

    public void testDemoRename() throws Exception{
        writeObjectToXml();
        renameClassAndFieldName();
        Object o = readNewObjectFromXml();

        assertTrue(o instanceof Map);
        assertTrue(((Map)o).get("chenhe") instanceof Person);
        Person person = (Person)((Map)o).get("chenhe");

        assertTrue(PersonOld1.getChenHe().getNameOld().equals(person.getName()));
        assertTrue(PersonOld1.getChenHe().getAge() == person.getAge());
        assertTrue(PersonOld1.getChenHe().getMale() == person.getMale());
    }

    private void writeObjectToXml() {
        Map<String,PersonOld1> personOlds = new HashMap<String, PersonOld1>();
        personOlds.put("chenhe",PersonOld1.getChenHe());
        personOlds.put("ab",PersonOld1.getAB());
        XMLWriterTest.generate(personOlds, "PersonRenameDemo");
    }

    private void renameClassAndFieldName() {
        //Do nothing
        //这里建了新的Person 并且字段nameOld改成了name
    }

    private Object readNewObjectFromXml() throws Exception{
        Class objClass = HashMap.class;
        Object o;
        o = BIConstructorUtils.forceConstructObject(objClass);
        o = XMLWriterTest.get(o, "PersonRenameDemo");
        return o;
    }

}
