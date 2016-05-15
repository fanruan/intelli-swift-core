/**
 * @class BIConf.Views
 * @extends BI.WRouter
 */
BIConf.Views = new (BI.inherit( BI.WRouter, {
    routes: {
        "" : "BIConf.View",
        "/BI-Permissions_Man" : "BIConf.PermissionManageView",
        "/BI-Packages_Man" : "BIConf.AllBusinessPackagesPaneView",
        "/BI-Data_Connection_Man" : "BIConf.DataLinkPaneView",
        "/BI-Multi_Path_Man" : "BIConf.MultiRelationView",
        "/BI-Cube_Updates_Setting" : "BIConf.UpdateCubePaneView",
        "/BI-Packages_Man/packageManagePane" : "BIConf.BusinessPackageGroupView",
        "/BI-Permissions_Man" : "BIConf.PermissionManageView"
    }
}));
