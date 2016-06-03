package com.fr.bi.conf.report.widget;

import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.json.JSONArray;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

import java.util.List;

public class RelationColumnKey implements JSONCreator{


    /**
     *
     */
    private static final long serialVersionUID = 434756052109265970L;
    protected List<BITableSourceRelation> relations;
    protected ICubeFieldSource field;

    public RelationColumnKey(ICubeFieldSource define,
                             List<BITableSourceRelation> relations) {
        field = define;
        this.relations = relations;
    }

    

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((relations == null) ? 0 : relations.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationColumnKey)) return false;

        RelationColumnKey that = (RelationColumnKey) o;

        if (relations != null ? !relations.equals(that.relations) : that.relations != null) return false;
        return !(field != null ? !field.equals(that.field) : that.field != null);

    }

    public List<BITableSourceRelation> getRelations() {
        return relations;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = field.createJSON();
        List<BITableSourceRelation> relations = getRelations();
        JSONArray ja = new JSONArray();
        /**
         * Connery:Pony说不用createJSON，但是外面有调用的，删不掉。
         */
//        if (relations != null) {
//            for (BITableSourceRelation relation : relations) {
//                ja.put(relation.createJSON());
//            }
//        }
        jo.put("relation", ja);
        return jo;
    }

}
