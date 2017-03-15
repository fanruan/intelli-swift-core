/**
 *
 */
package com.finebi.cube.conf.timer;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;
/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public class UpdateFrequency implements XMLable, JSONTransform {

    private static final long serialVersionUID = -5054815883317806025L;
    public static String XML_TAG = "UpdateFrequency";


    private int updatehour;
    private int frequency = DBConstant.UPDATE_FREQUENCY.EVER_DAY;

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getUpdatehour() {
        return updatehour;
    }

    public void setUpdatehour(int updatehour) {
        this.updatehour = updatehour;
    }

    public UpdateFrequency() {

    }

    public UpdateFrequency(int updatehour, int frequency) {
        this.updatehour = updatehour;
        this.frequency = frequency;
    }

    public int getUpdateHour() {
        return updatehour;
    }

    public int getFrequency() {
        return frequency;
    }

    /* (non-Javadoc)
     * @see com.fr.stable.xml.XMLReadable#readXML(com.fr.stable.xml.XMLableReader)
     */
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            this.updatehour = reader.getAttrAsInt("updatehour", 0);
            this.frequency = reader.getAttrAsInt("frequency", DBConstant.UPDATE_FREQUENCY.EVER_DAY);
        }
    }

    /* (non-Javadoc)
     * @see com.fr.stable.xml.XMLWriter#writeXML(com.fr.stable.xml.XMLPrintWriter)
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {

        writer.startTAG(XML_TAG).attr("updatehour", updatehour).attr("frequency", frequency).end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /* (non-Javadoc)
     * @see com.fr.json.ParseJSON#parseJSON(com.fr.json.JSONObject)
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("updatehour")) {
            this.updatehour = jo.getInt("updatehour");
        }
        if (jo.has("frequency")) {
            this.frequency = jo.getInt("frequency");
        }
    }

    /* (non-Javadoc)
     * @see com.fr.json.CreateJSON#createJSON()
     */
    @Override
    public JSONObject createJSON() throws Exception {

        JSONObject jo = new JSONObject();
        jo.put("updatehour", updatehour);
        jo.put("frequency", frequency);
        return jo;
    }
}