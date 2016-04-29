/**
 * Created by roy on 15/11/4.
 */
BI.CustomGroupAllFieldsPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomGroupAllFieldsPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-group-all-fields-pane",
            enableCheckGroup: false,
            unGroupedItems: [],
            groupedItems: []
        })
    },

    _init: function () {
        var self = this, o = this.options;
        BI.CustomGroupAllFieldsPane.superclass._init.apply(this, arguments);

        this.ungroupedPane = BI.createWidget({
            type: "bi.etl_add_group_field_custom_group_field_pane",
            items: o.unGroupedItems,
            nodeType: "bi.arrow_group_node"
        });

        this.groupedPane = BI.createWidget({
            type: "bi.etl_add_group_field_custom_group_field_pane",
            items: o.groupedItems,
            enableCheckGroup: o.enableCheckGroup,
            validationChecker: function (v, id) {
                var groupMap = self.groupedPane.getGroupMap();
                var ungroupedFieldMap = self.ungroupedPane.getFieldMap();
                var result = BI.findKey(groupMap, function (groupID, groupName) {
                    return groupName === v && id != groupID
                });
                result = result || BI.findKey(ungroupedFieldMap, function (fieldID, fieldName) {
                        return fieldName === v
                    });
                if (BI.isNotNull(result)) {
                    return false
                } else {
                    return true
                }
            },
            nodeType: o.nodeType
        });

        this.ungroupedPane.on(BI.CustomGroupFieldPane.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CustomGroupAllFieldsPane.EVENT_CHANGE, obj)
        });

        this.groupedPane.on(BI.CustomGroupFieldPane.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CustomGroupAllFieldsPane.EVENT_CHANGE, obj)
        });


        this.groupedPane.on(BI.CustomGroupFieldPane.EVENT_EMPTY_GROUP, function (fieldID, fieldName) {
            self.ungroupedPane.addFieldWidget(fieldID, fieldName, BI.i18nText("BI-Ungrouped_China"));
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

    deleteFieldWidget: function (fieldID) {
        var self = this;
        this.ungroupedPane.deleteFieldWidget(fieldID);
        this.groupedPane.deleteFieldWidget(fieldID);
        var id = self._getFieldID(fieldID);
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

    getUngroupedFieldMap: function () {
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

    getGroupOfFieldMap: function () {
        var self = this;
        return BI.deepClone(self.groupedPane.getGroupOfFieldMap());
    }


});
BI.CustomGroupAllFieldsPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_add_group_field_custom_group_all_fields_pane", BI.CustomGroupAllFieldsPane);