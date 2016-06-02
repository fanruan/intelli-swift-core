package com.finebi.cube.conf.pack.data;

import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.container.BISetContainer;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.FCloneable;

import java.util.*;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BIBusinessPackage<T extends BusinessTable> extends BISetContainer<T> implements JSONTransform, FCloneable, IBusinessPackageGetterService<T> {

    protected BIUser owner;
    protected BIPackageName name;
    protected long position;

    /**
     * 唯一标准，包括equals和hashcode
     */
    protected BIPackageID ID;


    public BIBusinessPackage(BIPackageID ID, BIPackageName name, BIUser owner, long position) {
        this.ID = ID;
        this.name = name;
        this.owner = owner;
        this.position = position;
    }

    public long getPosition() {
        return position;
    }

    @Override
    public BIUser getOwner() {
        return owner;
    }

    @Override
    public BIPackageName getName() {
        return name;
    }

    public void setName(BIPackageName name) {
        this.name = name;
    }

    @Override
    public BIPackageID getID() {
        return ID;
    }

    public BIBusinessPackage(BIPackageID id) {
        this(id, BIPackageName.DEFAULT, BIUser.DEFALUT, System.currentTimeMillis());
    }

    protected abstract T createTable();

    protected Collection initCollection() {
        return new LinkedHashSet<T>();
    }

    @Override
    public boolean isNeed2BuildCube(BIBusinessPackage targetPackage) {
        if (size() == targetPackage.size()) {
            Iterator<T> currentIt = container.iterator();
            while (currentIt.hasNext()) {
                T currentTable = currentIt.next();
                Iterator<T> targetIt = targetPackage.container.iterator();
                while (targetIt.hasNext()) {
                    T targetTable = targetIt.next();
                    if (ComparatorUtils.equals(targetTable, currentTable)) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Set<T> getBusinessTables() {
        return getContainer();
    }

    public void addBusinessTable(T biBusinessTable) {
        add(biBusinessTable);
    }

    public void removeBusinessTable(T biBusinessTable) {
        remove(biBusinessTable);
    }

    public void removeBusinessTableByID(BITableID tableID) {
        try {
            T table = getSpecificTable(tableID);
            removeBusinessTable(table);
        } catch (BITableAbsentException ignore) {

        }
    }

    @Override
    public T getSpecificTable(BITableID tableID) throws BITableAbsentException {
        Iterator<T> it = getContainer().iterator();
        while (it.hasNext()) {
            T result = it.next();
            if (ComparatorUtils.equals(result.getID(), tableID)) {
                return result;
            }
        }
        throw new BITableAbsentException();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BIBusinessPackage{");
        sb.append("owner=").append(owner);
        sb.append(", name=").append(name);
        sb.append(", ID=").append(ID);
        sb.append('}');
        return sb.toString();
    }

    /**
     * 输出表名json
     *
     * @return json对象
     * @throws
     */

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("name", name.getValue());
        jo.put("id", ID.getIdentityValue());

        JSONArray result = new JSONArray();
        Set<T> tables = getBusinessTables();
        Iterator<T> it = tables.iterator();
        while (it.hasNext()) {
            T table = it.next();
            result.put(table.createJSON());
        }
        jo.put("tables", result);
        jo.put("position", this.position);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        //this.setName(jo.optString("package_name"));
        JSONArray ja = jo.optJSONArray("data");
        clear();
        for (int i = 0; i < ja.length(); i++) {
            T table = createTable();
            JSONObject tableJson = ja.optJSONObject(i);
            table.parseJSON(tableJson);
            List<String> fieldNames = new ArrayList<String>();

            if (tableJson.has("fields")) {
                table.setFields(this.parseField(tableJson.getJSONArray("fields"), table));
            }
            add(table);
        }
    }

    private List<BusinessField> parseField(JSONArray fieldsJA, BusinessTable table) {
        List<BusinessField> fields = new ArrayList<BusinessField>();
        for (int i = 0; i < fieldsJA.length(); i++) {
            try {
                JSONArray ja = fieldsJA.getJSONArray(i);
                for (int j = 0; j < ja.length(); j++) {
                    JSONObject fieldJO = ja.getJSONObject(j);
                    String field_name =null;
                    int fieldSize =0;
                    int classType = 0;
                    if (fieldJO.has("field_name")){
                     field_name =  fieldJO.getString("field_name");}
                    if (fieldJO.has("class_type")){
                        classType = fieldJO.getInt("class_type");
                    }
                    if (fieldJO.has("field_size")){
                        fieldSize = fieldJO.getInt("field_size");
                    }
                    BIBusinessField field = new BIBusinessField(table, new BIFieldID(fieldJO.getString("id")),
                         field_name , classType,
                            fieldSize, fieldJO.optBoolean("is_usable"), fieldJO.optBoolean("is_enable"));
                    fields.add(field);
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
        return fields;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BIBusinessPackage cloned = (BIBusinessPackage) super.clone();

        Iterator<T> it = getContainer().iterator();
        while (it.hasNext()) {
            cloned.addBusinessTable((T) it.next().clone());
        }

        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIBusinessPackage)) {
            return false;
        }

        BIBusinessPackage<?> that = (BIBusinessPackage<?>) o;

        return !(ID != null ? !ComparatorUtils.equals(ID, that.ID) : that.ID != null);


    }

    @Override
    public int hashCode() {
        return ID != null ? ID.hashCode() : 0;
    }
}