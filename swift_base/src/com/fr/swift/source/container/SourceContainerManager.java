package com.fr.swift.source.container;

/**
 * This class created on 2017-12-19 11:43:14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SourceContainerManager {

    private DataSourceContainer dataSourceContainer;
    private RelationSourceContainer relationSourceContainer;
    private PathSourceContainer pathSourceContainer;

    public SourceContainerManager() {
        this.dataSourceContainer = new DataSourceContainer();
        this.relationSourceContainer = new RelationSourceContainer();
        this.pathSourceContainer = new PathSourceContainer();
    }

    public DataSourceContainer getDataSourceContainer() {
        return dataSourceContainer;
    }

    public RelationSourceContainer getRelationSourceContainer() {
        return relationSourceContainer;
    }

    public PathSourceContainer getPathSourceContainer() {
        return pathSourceContainer;
    }


}
