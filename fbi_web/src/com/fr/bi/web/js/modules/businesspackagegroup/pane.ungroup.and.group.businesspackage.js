/**
 * Created by roy on 15/11/4.
 */
BI.BusinessUngroupAndGroupPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.BusinessUngroupAndGroupPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-group-all-fields-pane",
            enableCheckGroup: false,
            disableAdd: false,
            deleteInvisible: false,
            unGroupedItems: [],
            groupedItems: [],
            forceNotSelected: false
        })
    },

    _init: function () {
        var self = this, o = this.options;
        BI.BusinessUngroupAndGroupPane.superclass._init.apply(this, arguments);

        this.ungroupedPane = BI.createWidget({
            type: "bi.business_package_group_pane",
            items: o.unGroupedItems,
            disableAdd: o.disableAdd,
            deleteInvisible: o.deleteInvisible,
            nodeType: "bi.arrow_group_node",
            forceNotSelected: o.forceNotSelected
        });

        this.groupedPane = BI.createWidget({
            type: "bi.business_package_group_pane",
            items: o.groupedItems,
            enableCheckGroup: o.enableCheckGroup,
            disableAdd: o.disableAdd,
            deleteInvisible: o.deleteInvisible,
            nodeType: o.nodeType,
            forceNotSelected: o.forceNotSelected
        });

        this.ungroupedPane.on(BI.BusinessPackageGroupPane.EVENT_CHANGE, function (groupID, packID) {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_CHANGE, "", packID)
        });

        this.groupedPane.on(BI.BusinessPackageGroupPane.EVENT_CHANGE, function (groupID, packID) {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_CHANGE, groupID, packID)
        });

        this.ungroupedPane.on(BI.BusinessPackageGroupPane.EVENT_CLICK_DELETE, function () {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_CLICK_DELETE)
        });

        this.groupedPane.on(BI.BusinessPackageGroupPane.EVENT_CLICK_DELETE, function () {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_CLICK_DELETE)
        });

        this.ungroupedPane.on(BI.BusinessPackageGroupPane.EVENT_CLICK_ADD, function (groupObj) {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_CLICK_ADD, {gid: "", groupName: groupObj.groupName})
        });

        this.groupedPane.on(BI.BusinessPackageGroupPane.EVENT_CLICK_ADD, function (groupObj) {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_CLICK_ADD, groupObj)
        });

        this.ungroupedPane.on(BI.BusinessPackageGroupPane.EVENT_PACKAGE_DELETE, function (obj) {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_PACKAGE_DELETE, obj)
        });

        this.groupedPane.on(BI.BusinessPackageGroupPane.EVENT_PACKAGE_DELETE, function (obj) {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_PACKAGE_DELETE, obj)
        });

        this.groupedPane.on(BI.BusinessPackageGroupPane.EVENT_GROUP_NAME_CHANGE, function () {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_GROUP_NAME_CHANGE)
        });

        this.groupedPane.on(BI.BusinessPackageGroupPane.EVENT_GROUP_NAME_CONFIRM, function () {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_GROUP_NAME_CONFIRM)
        });



        this.groupedPane.on(BI.BusinessPackageGroupPane.EVENT_EMPTY_GROUP, function (fieldID, fieldName) {
            self.ungroupedPane.addFieldWidget(fieldID, fieldName, BI.i18nText("BI-Ungrouped_China"));
        });

        this.ungroupedPane.on(BI.BusinessPackageGroupPane.EVENT_EDITOR_CONFIRM, function (packageName, packageID) {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_EDITOR_CONFIRM, packageName, packageID)
        });

        this.groupedPane.on(BI.BusinessPackageGroupPane.EVENT_EDITOR_CONFIRM, function (packageName, packageID) {
            self.fireEvent(BI.BusinessUngroupAndGroupPane.EVENT_EDITOR_CONFIRM, packageName, packageID)
        });


        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [
                self.ungroupedPane,
                self.groupedPane
            ]
        })
    },

    _getGroupID: function (id) {
        var ids = id.split("_");
        return ids[0];
    },

    _getFieldID: function (id) {
        var ids = id.split("_");
        return ids[1];
    },

    createItemFromEmptyGroupMap: function () {
        return this.groupedPane.createItemFromEmptyGroupMap();
    },

    createItemFromGroupMap: function () {
        return this.groupedPane.createItemFromGroupMap();
    },

    createItemFromUngroupedFieldMap: function (ungroupedFieldMap) {
        var ungroupedItem = this.ungroupedPane.createItemFromFieldMap(ungroupedFieldMap);
        return ungroupedItem;
    },

    createItemFromGroupedFieldMap: function (groupedFieldMap) {
        var groupedItem = this.groupedPane.createItemFromFieldMap(groupedFieldMap);
        return groupedItem;
    },

    createGroupWidget: function (groupItem) {
        this.groupedPane.createGroupWidget(groupItem);
    },

    empty: function () {
        this.ungroupedPane.empty();
        this.groupedPane.empty();
    },

    populate: function (ungroupedItems, groupedItems) {
        this.ungroupedPane.populate(ungroupedItems);
        this.groupedPane.populate(groupedItems);
    },

    setFieldSelectedTrue: function (fieldID) {
        this.ungroupedPane.setFieldSelectedTrue(fieldID);
        this.groupedPane.setFieldSelectedTrue(fieldID);
    },

    setFieldSelectedFalse: function (fieldID) {
        this.ungroupedPane.setFieldSelectedFalse(fieldID);
        this.groupedPane.setFieldSelectedFalse(fieldID);
    },

    deleteGroupWidget: function (groupID) {
        this.groupedPane.deleteGroupWidget(groupID);
    },

    addGroupWidget: function (groupName) {
        this.groupedPane.addGroupWidget(groupName);
    },

    addFieldWidget: function (widgetID, fieldName, groupName) {
        this.groupedPane.addFieldWidget(widgetID, fieldName, groupName);
    },

    deleteFieldWidget: function (fieldID, checkEmpty) {
        var self = this;
        this.ungroupedPane.deleteFieldWidget(fieldID, checkEmpty);
        this.groupedPane.deleteFieldWidget(fieldID, checkEmpty);
    },

    doRedMark: function (keyword, fieldID) {
        this.groupedPane.doRedMark(keyword, fieldID);
        this.ungroupedPane.doRedMark(keyword, fieldID);
    },

    doHighLight: function (fieldID) {
        this.groupedPane.doHighLight(fieldID);
    },

    setFieldRightValue: function (value, fieldID) {
        this.groupedPane.setFieldRightValue(value, fieldID);
    },

    getSelectedFieldMap: function () {
        var self = this;
        var ungroupedMap = BI.deepClone(self.ungroupedPane.getSelectedFieldMap());
        var groupedMap = BI.deepClone(self.groupedPane.getSelectedFieldMap());
        BI.each(groupedMap, function (id, fieldName) {
            ungroupedMap[id] = groupedMap[id];
        });
        return ungroupedMap;
    },

    getGroupedSelectedFieldMap: function () {
        var self = this;
        var selectedMap = BI.deepClone(self.groupedPane.getSelectedFieldMap());
        return selectedMap;
    },

    getUnGroupedSelectedFieldMap: function () {
        var self = this;
        var selectedMap = BI.deepClone(self.ungroupedPane.getSelectedFieldMap());
        return selectedMap;
    },


    getGroupedFieldMap: function () {
        return this.groupedPane.getFieldMap();
    },

    getUngrouedFieldMap: function () {
        return this.ungroupedPane.getFieldMap();
    },

    getAllFieldMap: function () {
        var self = this;
        var groupedFieldMap = BI.deepClone(self.groupedPane.getFieldMap());
        var ungroupedFieldMap = BI.deepClone(self.ungroupedPane.getFieldMap());
        BI.each(groupedFieldMap, function (id, fieldName) {
            ungroupedFieldMap[id] = groupedFieldMap[id];
        });
        return ungroupedFieldMap;
    },

    getGroupMap: function () {
        return this.groupedPane.getGroupMap();
    },


    setUngroupedGroupName: function (newname, oldname) {
        this.ungroupedPane.setGroupName(newname, oldname);
    },

    setGroupedGroupName: function (newname, oldname) {
        this.groupedPane.setGroupName(newname, oldname);
    }


});
BI.BusinessUngroupAndGroupPane.EVENT_CHANGE = "EVENT_CHANGE";
BI.BusinessUngroupAndGroupPane.EVENT_CLICK_DELETE = "EVENT_CLICK_DELETE";
BI.BusinessUngroupAndGroupPane.EVENT_PACKAGE_DELETE = "EVENT_PACKAGE_DELETE";
BI.BusinessUngroupAndGroupPane.EVENT_CLICK_ADD = "EVENT_CLICK_ADD";
BI.BusinessUngroupAndGroupPane.EVENT_GROUP_NAME_CHANGE = "EVENT_GROUP_NAME_CHANGE";
BI.BusinessUngroupAndGroupPane.EVENT_GROUP_NAME_CONFIRM = "EVENT_GROUP_NAME_CONFIRM";
BI.BusinessUngroupAndGroupPane.EVENT_EDITOR_CONFIRM = "EVENT_EDITOR_CONFIRM";
$.shortcut("bi.business_package_ungroup_and_group_pane", BI.BusinessUngroupAndGroupPane);