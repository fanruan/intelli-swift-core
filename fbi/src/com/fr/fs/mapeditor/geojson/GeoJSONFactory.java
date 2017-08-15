package com.fr.fs.mapeditor.geojson;

import com.fr.base.FRContext;
import org.codehaus.myjackson.JsonGenerationException;
import org.codehaus.myjackson.JsonNode;
import org.codehaus.myjackson.map.DeserializationConfig;
import org.codehaus.myjackson.map.JsonMappingException;
import org.codehaus.myjackson.map.ObjectMapper;
import org.codehaus.myjackson.type.JavaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GeoJSONFactory {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static JSONMapper create(String json, String envPath) throws Exception {
        MAPPER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode node = MAPPER.readTree(json);

        node = node.get("geoJSON") == null ? node : node.get("geoJSON");
        FeatureCollection geoJSON = readFeatureCollection(node);
        JSONMapper jsonMapper = new JSONMapper(geoJSON);

        jsonMapper.setPath(envPath);

        if(node.get("imageSuffix") != null) {
            String imageSuffix = node.get("imageSuffix").getValueAsText();
            jsonMapper.setImageSuffix(imageSuffix);
        }
        if(node.get("imageString") != null) {
            String imageString = node.get("imageString").getValueAsText();
            jsonMapper.setImageString(imageString);
        }
        if(node.get("imageWidth") != null) {
            int imageWidth = node.get("imageWidth").getValueAsInt();
            jsonMapper.setImageWidth(imageWidth);
        }
        if(node.get("imageHeight") != null) {
            int imageHeight = node.get("imageHeight").getValueAsInt();
            jsonMapper.setImageHeight(imageHeight);
        }

        return jsonMapper;
    }

    public static FeatureCollection createGEO(String json){
        try {
            MAPPER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode node = MAPPER.readTree(json);
            return readFeatureCollection(node);
        }catch (Exception e){
            FRContext.getLogger().error(e.getMessage(), e);
        }
        return null;
    }
    
    private static FeatureCollection readFeatureCollection(JsonNode node)
            throws IOException, ClassNotFoundException {
        Iterator<JsonNode> it = node.get("features").iterator();
        List<Feature> features = new ArrayList<Feature>();
        while (it.hasNext()) {
            JsonNode jFeature = it.next();
            features.add(readFeature(jFeature));
        }
        
        return new FeatureCollection(features.toArray(new Feature[features.size()]));
    }
    
    private static Feature readFeature(JsonNode node) throws IOException, ClassNotFoundException {
        JsonNode geometryNode = node.get("geometry");
        JavaType javaType = MAPPER.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
        Map<String, Object> properties = MAPPER.readValue(node.get("properties").traverse(), javaType);
        String type = geometryNode.get("type").getValueAsText();
        Geometry geometry = readGeometry(geometryNode, type);
        return new Feature(geometry, properties);
    }
    
    private static Geometry readGeometry(JsonNode node, String type) throws IOException, ClassNotFoundException {
        return  (Geometry) MAPPER.readValue(node.traverse(), Class.forName("com.fr.fs.plugin.mapeditor.geojson." + type));
    }

    public static String toString(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (JsonGenerationException e) {
            return "Unhandled exception occured when serializing this instance";
        } catch (JsonMappingException e) {
            return "Unhandled exception occured when serializing this instance";
        } catch (IOException e) {
            return "Unhandled exception occured when serializing this instance";
        }
    }
}
