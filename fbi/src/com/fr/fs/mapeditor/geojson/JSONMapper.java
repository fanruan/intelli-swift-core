package com.fr.fs.mapeditor.geojson;

import com.fr.base.FRContext;
import com.fr.cache.Attachment;
import com.fr.cache.AttachmentSource;
import com.fr.chart.base.ChartBaseUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.plugin.chart.vanchart.imgevent.ImageUtils;
import com.fr.stable.ArrayUtils;
import com.fr.stable.StringUtils;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mitisky on 16/7/12.
 */
public class JSONMapper {
    private String path;
    private String imageString;
    private String imageSuffix;
    private int imageWidth;
    private int imageHeight;
    private Attachment attachment;

    private FeatureCollection geoJSON;


    public static JSONMapper addJSONMapper(JSONMapper a, JSONMapper b){

        if(a == null || b == null){
            return a == null ? b : a;
        }

        Feature[] features = ArrayUtils.addAll(a.getGeoJSON().getFeatures(), b.getGeoJSON().getFeatures());

        FeatureCollection fs = new FeatureCollection(features);

        JSONMapper mapper = new JSONMapper(fs);

        mapper.setPath(a.getPath());
        mapper.setImageWidth(a.getImageWidth());
        mapper.setImageHeight(a.getImageHeight());
        mapper.setImageString(a.getImageString());
        mapper.setImageSuffix(a.getImageSuffix());

        return mapper;
    }

    public JSONMapper(FeatureCollection geoJSON){
        this.geoJSON = geoJSON;
    }

    public String toString(){
        try {
            if(this.geoJSON != null){
                JSONObject jo = new JSONObject(GeoJSONFactory.toString(geoJSON));
                if(imageString != null){
                    jo.put("imageString", imageString);
                    jo.put("imageSuffix", imageSuffix);
                    jo.put("imageWidth", imageWidth);
                    jo.put("imageHeight", imageHeight);
                }
                return jo.toString();
            }
        }catch (JSONException e){
            FRContext.getLogger().error(e.getMessage(), e);
        }
        return StringUtils.EMPTY;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImageString() {
        return imageString;
    }

    public boolean isImageMap(){
        return StringUtils.isNotBlank(imageString);
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
//        this.calculateEmbID();
    }

    public int getImageWidth(){
        return this.imageWidth;
    }

    public int getImageHeight(){
        return this.imageHeight;
    }

    public void setImageWidth(int imageWidth){
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(int imageHeight){
        this.imageHeight = imageHeight;
    }

    public void setImageSuffix(String imageSuffix) {
        this.imageSuffix = imageSuffix;
    }

    public String getImageSuffix(){
        return this.imageSuffix;
    }

    public void setGeoJSON(FeatureCollection geoJSON) {
        this.geoJSON = geoJSON;
    }

//    private void calculateEmbID() {
//        new SwingWorker<Void, Double>(){
//            @Override
//            protected Void doInBackground() throws Exception {
//                 JSONMapper.this.getEmbID();
//                 return null;
//            }
//
//        }.execute();
//    }

    public String getEmbID() {
        String embID = StringUtils.EMPTY;

        if (this.attachment == null && StringUtils.isNotEmpty(this.imageString)) {
            Image customImage = ImageUtils.generateImage(this.imageString);

            if (customImage == null) {
                return embID;
            }
            embID = ChartBaseUtils.addImageAsEmb(customImage);
            this.attachment = AttachmentSource.getAttachment(embID);
        }

        if(this.attachment != null) {
            embID = this.attachment.getID();
            AttachmentSource.putAttachment(embID, this.attachment);
        }
        return embID;
    }

    public Map<String, List<List<List<double[]>>>> calculateOrderCoordinates() {
        return geoJSON.calculateOrderCoordinates();
    }

    /**
     * 获取所有区域的所有坐标
     * @return 所有区域名及其对应的有序坐标
     */
    public Set<String> calculateAreaNames() {
        return geoJSON.calculateAreaNames();
    }

    public Map<String, List<Object>> calculateCenter() {
        return geoJSON.calculateCenter();
    }

    public FeatureCollection getGeoJSON(){
        return geoJSON;
    }
}
