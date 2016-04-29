package com.fr.bi.conf.report.widget;

import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.List;

public class RelationColumnKey extends BIField {


    /**
     *
     */
    private static final long serialVersionUID = 434756052109265970L;
    protected List<BITableSourceRelation> relations;


    public RelationColumnKey(BIField define,
                             List<BITableSourceRelation> relations) {
        super(define);
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

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RelationColumnKey other = (RelationColumnKey) obj;
        if (relations == null) {
            if (other.relations != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(relations, other.relations)) {
            return false;
        }
        return true;
    }

    public List<BITableSourceRelation> getRelations() {
        return relations;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        List<BITableSourceRelation> relations = getRelations();
        JSONArray ja = new JSONArray();
        if (relations != null) {
            for (BITableSourceRelation relation : relations) {
                ja.put(relation.createJSON());
            }
        }
        jo.put("relation", ja);
        return jo;
    }

}