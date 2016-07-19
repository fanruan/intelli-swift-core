package com.finebi.cube.location;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.exception.BICubeResourceDuplicateException;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;
import com.fr.bi.stable.utils.code.BILogger;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/3/8.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeResourceRetrieval implements ICubeResourceRetrievalService {
    private ICubeConfiguration cubeConfiguration;
    private Map<String, ICubeResourceLocation> locationMap;

    public BICubeResourceRetrieval(ICubeConfiguration cubeConfiguration) {
        this.cubeConfiguration = cubeConfiguration;
        locationMap = new HashMap<String, ICubeResourceLocation>();
    }

    @Override
    public ICubeResourceLocation retrieveResource(String sourceID) throws BICubeResourceAbsentException {
        synchronized (locationMap) {
            if (locationMap.containsKey(sourceID)) {
                return locationMap.get(sourceID);
            } else {
                throw new BICubeResourceAbsentException();
            }
        }
    }

    @Override
    public ICubeResourceLocation retrieveRootResource(String sourceID) throws BICubeResourceAbsentException {
        synchronized (locationMap) {
            try {
                return retrieveResource(sourceID);
            } catch (BICubeResourceAbsentException ignore) {
                try {
                    ICubeResourceLocation rootLocation = buildRootLocation(sourceID);
                    registerResource(sourceID, rootLocation);
                    return rootLocation;
                } catch (URISyntaxException changeSyntax) {
                    BILogger.getLogger().error(changeSyntax.getMessage(), changeSyntax);

                } catch (BICubeResourceDuplicateException ignoreDuplicate) {
                    BILogger.getLogger().error("ignore below error");
                    BILogger.getLogger().error(ignoreDuplicate.getMessage(), ignoreDuplicate);
                }
                throw new BICubeResourceAbsentException("Please check Table Source:" + sourceID);
            }
        }
    }

    private ICubeResourceLocation buildLocation(String rootURI, String child) throws URISyntaxException {
        return new BICubeLocation(rootURI, child);
    }

    @Override
    public ICubeResourceLocation retrieveResource(ITableKey table) throws BICubeResourceAbsentException {
        String tableSourceID = calculateTableSourceID(table);
        synchronized (locationMap) {
            try {
                return retrieveResource(tableSourceID);
            } catch (BICubeResourceAbsentException ignore) {
                try {
                    ICubeResourceLocation tableLocation = buildTableLocation(tableSourceID);
                    registerResource(tableSourceID, tableLocation);
                    return tableLocation;
                } catch (URISyntaxException changeSyntax) {
                    BILogger.getLogger().error(changeSyntax.getMessage(), changeSyntax);

                } catch (BICubeResourceDuplicateException ignoreDuplicate) {
                    BILogger.getLogger().error("ignore below error");
                    BILogger.getLogger().error(ignoreDuplicate.getMessage(), ignoreDuplicate);
                }
                throw new BICubeResourceAbsentException("Please check Table Source:" + table.toString());
            }
        }
    }

    private ICubeResourceLocation buildTableLocation(String sourceID) throws URISyntaxException {
        return buildLocation(cubeConfiguration.getRootURI().getPath(), sourceID);
    }

    private ICubeResourceLocation buildRootLocation(String sourceID) throws URISyntaxException {
        return buildLocation(cubeConfiguration.getRootURI().getPath(), sourceID);
    }

    private ICubeResourceLocation buildRelationLocation(ITableKey tableKey, String relationSourceID) throws URISyntaxException, BICubeResourceAbsentException {
        ICubeResourceLocation tableLocation = retrieveResource(tableKey.getSourceID());
        return tableLocation.buildChildLocation(RELATION_NAME).buildChildLocation(relationSourceID);
    }

    private ICubeResourceLocation buildFieldRelationLocation(String fieldSourceID, String relationSourceID) throws URISyntaxException, BICubeResourceAbsentException {
        ICubeResourceLocation tableLocation = retrieveResource(fieldSourceID);
        return tableLocation.buildChildLocation(RELATION_NAME).buildChildLocation(relationSourceID);
    }

    void checkParentLocation(ITableKey table, BIColumnKey field) {
        checkParentLocation(table);
        String fieldSourceID = calculateFieldSourceID(table, field);
        if (!locationMap.containsKey(fieldSourceID)) {
            try {
                retrieveResource(table, field);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    void checkParentLocation(ITableKey table) {
        String tableSourceID = calculateTableSourceID(table);
        if (!locationMap.containsKey(tableSourceID)) {
            try {
                retrieveResource(table);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    String calculateFieldSourceID(ITableKey table, BIKey field) {
        return calculateSourceID(table.getSourceID(), field.getKey());
    }

    String calculateTableSourceID(ITableKey table) {
        return table.getSourceID();
    }

    @Override
    public ICubeResourceLocation retrieveResource(ITableKey table, BIColumnKey field) throws BICubeResourceAbsentException, URISyntaxException {
        String sourceID = calculateFieldSourceID(table, field);
        synchronized (locationMap) {
            try {
                return retrieveResource(sourceID);
            } catch (BICubeResourceAbsentException ignore) {
                ICubeResourceLocation tableLocation = retrieveResource(table);
                try {
                    ICubeResourceLocation resourceLocation = buildLocation(tableLocation.getAbsolutePath(), sourceID);
                    registerResource(sourceID, resourceLocation);
                    return resourceLocation;
                } catch (URISyntaxException changeSyntax) {
                    BILogger.getLogger().error(changeSyntax.getMessage(), changeSyntax);

                } catch (BICubeResourceDuplicateException ignoreDuplicate) {
                    BILogger.getLogger().error("Please check below error");
                    BILogger.getLogger().error(ignoreDuplicate.getMessage(), ignoreDuplicate);
                }
                throw new BICubeResourceAbsentException("Please check Table Source:" + table.toString());
            }
        }
    }

    String calculateFieldRelationSourceID(ITableKey table, BIColumnKey field, BICubeTablePath tableRelationPath) {
        return calculateSourceID(table.getSourceID(), field.getKey(), tableRelationPath.getSourceID());
    }

    @Override
    public ICubeResourceLocation retrieveResource(ITableKey table, BIColumnKey field, BICubeTablePath tableRelationPath) throws BICubeResourceAbsentException, URISyntaxException {
        String sourceID = calculateFieldRelationSourceID(table, field, tableRelationPath);
        synchronized (locationMap) {
            try {
                return retrieveResource(sourceID);
            } catch (BICubeResourceAbsentException ignore) {
                checkParentLocation(table, field);
                try {
                    ICubeResourceLocation resourceLocation = buildFieldRelationLocation(calculateFieldSourceID(table, field), sourceID);
                    registerResource(sourceID, resourceLocation);
                    return resourceLocation;
                } catch (URISyntaxException changeSyntax) {
                    BILogger.getLogger().error(changeSyntax.getMessage(), changeSyntax);
                } catch (BICubeResourceDuplicateException ignoreDuplicate) {
                    BILogger.getLogger().error("Please check below error");
                    BILogger.getLogger().error(ignoreDuplicate.getMessage(), ignoreDuplicate);
                } catch (BICubeResourceAbsentException e) {
                    BILogger.getLogger().error("Please check below error");
                    BILogger.getLogger().error(e.getMessage(), e);
                }
                throw new BICubeResourceAbsentException("Please check Table Source:" + table.toString());
            }
        }
    }

    String calculateTableRelationSourceID(ITableKey table, BICubeTablePath tableRelationPath) {
        return calculateSourceID(table.getSourceID(), tableRelationPath.getSourceID());
    }

    @Override
    public ICubeResourceLocation retrieveResource(ITableKey tableSource, BICubeTablePath tableRelationPath) throws BICubeResourceAbsentException, BITablePathEmptyException {

        String sourceID = calculateTableRelationSourceID(tableSource, tableRelationPath);
        synchronized (locationMap) {
            try {
                return retrieveResource(sourceID);
            } catch (BICubeResourceAbsentException ignore) {
                checkParentLocation(tableSource);
                ITableKey startTable = tableRelationPath.getStartTable();
                try {
                    ICubeResourceLocation resourceLocation = buildRelationLocation(startTable, sourceID);
                    registerResource(sourceID, resourceLocation);
                    return resourceLocation;
                } catch (URISyntaxException changeSyntax) {
                    BILogger.getLogger().error(changeSyntax.getMessage(), changeSyntax);

                } catch (BICubeResourceDuplicateException ignoreDuplicate) {
                    BILogger.getLogger().error("Please check below error");
                    BILogger.getLogger().error(ignoreDuplicate.getMessage(), ignoreDuplicate);
                } catch (BICubeResourceAbsentException ignoreAbsent) {
                    BILogger.getLogger().error("Please check below error");
                    BILogger.getLogger().error(ignoreAbsent.getMessage(), ignoreAbsent);
                }
                throw new BICubeResourceAbsentException("Please check Table Source:" + tableRelationPath.toString());
            }
        }
    }


    private String calculateSourceID(String... values) {
        return BIMD5Utils.getMD5String(values);
    }

    protected void registerResource(String sourceID, ICubeResourceLocation resourceLocation) throws BICubeResourceDuplicateException {
        synchronized (locationMap) {
            if (!locationMap.containsKey(sourceID)) {
                locationMap.put(sourceID, resourceLocation);
            } else {
                throw new BICubeResourceDuplicateException();
            }
        }
    }
}
