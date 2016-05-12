BI.BusinessPackageGroupPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.BusinessPackageGroupPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-business-package-group-pane",
            nodeType: "bi.arrow_group_node_delete",
            title: "",
            enableCheckGroup: false,
            disableAdd: false,
            deleteInvisible: false,
            forceNotSelected: false
        })
    },

    _init: function () {
        var self = this, o = this.options;
        BI.BusinessPackageGroupPane.superclass._init.apply(this, arguments);
        this.groupMap = {};
        this.fieldMap = {};
        this.groupWidgetMap = {};
        this.chosenFieldMap = {};
        this.fieldInGroupMap = {};
        this.groupOfFieldMap = {};


        this._initFields(o.items);
        var expanderItems = self.createItemFromFieldMap(self.fieldMap);
        var groupWidgets = [];

        BI.each(expanderItems, function (i, expanderItem) {
            groupWidgets.push(self.createGroupWidget(expanderItem));
        });


        this.fieldPane = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: groupWidgets
        });

        BI.each(self.fieldMap, function (fieldID, fieldName) {
            self.checkDuplicate(fieldID);
        })
    },


    _initFields: function (items) {
        var self = this;
        BI.each(items, function (i, item) {
            var group_id;
            if (!BI.isNotNull(item.id)) {
                group_id = BI.UUID();
                item.id = group_id;
            }
            group_id = item.id;
            BI.isNull(self.groupMap[group_id]) && (self.groupMap[group_id] = {});
            self.groupMap[group_id].init_time = item.init_time;
            self.groupMap[group_id].name = item.value;
            self.fieldInGroupMap[group_id] = {};
            BI.each(item.children, function (i_in, item_in) {
                var field_id;
                var id = BI.findKey(self.fieldMap, function (key, value) {
                    return value === item_in.value
                });

                if (!BI.isNotNull(item_in.id)) {
                    if (!BI.isNotNull(id)) {
                        field_id = self._createGroupFieldID(group_id, BI.UUID());
                    } else {
                        field_id = self._createGroupFieldID(group_id, self._getFieldID(id))
                    }
                    item_in.id = field_id;
                } else {
                    field_id = item_in.id;
                }
                self.fieldMap[field_id] = item_in.value;
                self.fieldInGroupMap[group_id][field_id] = item_in.value;

                if (!self.groupOfFieldMap[self._getFieldID(field_id)]) {
                    self.groupOfFieldMap[self._getFieldID(field_id)] = {};
                }
                self.groupOfFieldMap[self._getFieldID(field_id)][group_id] = self.groupMap[group_id].name;
            })
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

    _createGroupFieldID: function (groupID, fieldID) {
        return groupID + "_" + fieldID;
    },

    createItemFromEmptyGroupMap: function () {
        var self = this, expanderItems = [];
        BI.each(self.fieldInGroupMap, function (groupID, fields) {
            if (BI.size(fields) === 0) {
                var groupItem = {};
                groupItem.id = groupID;
                groupItem.value = self.groupMap[groupID].name;
                groupItem.children = [];
                expanderItems.push(groupItem);
            }
        });
        return expanderItems;
    },

    createItemFromFieldMap: function (fieldMap) {
        var self = this, expanderItems = [];
        BI.each(fieldMap, function (fieldID, fieldName) {
            var groupID = self._getGroupID(fieldID);

            var found = BI.find(expanderItems, function (i_in, item_in) {
                if (item_in.id === groupID) {
                    var fieldItem = {};
                    fieldItem.id = fieldID;
                    fieldItem.value = fieldName;
                    item_in.children.push(fieldItem);
                    return true;
                }
            });

            if (!found) {
                var groupItem = {};
                groupItem.id = groupID;
                groupItem.value = self.groupMap[groupID].name;
                groupItem.children = [];
                var fieldItem = {};
                fieldItem.id = fieldID;
                fieldItem.value = fieldName;
                groupItem.children.push(fieldItem);
                expanderItems.push(groupItem);
            }
        });
        return expanderItems;
    },

    createItemFromGroupMap: function () {
        var self = this, expanderItems = {};
        BI.each(self.groupMap, function (groupID, obj) {
            var groupItem = {};
            groupItem.name = obj.name;
            groupItem.children = [];
            groupItem.id = groupID;
            groupItem.init_time = obj.init_time;
            BI.each(self.fieldInGroupMap[groupID], function (fieldID, fieldName) {
                var fieldItem = {};
                fieldItem.id = fieldName;
                fieldItem.name = BI.Utils.getConfPackageNameByID(fieldName);
                groupItem.children.push(fieldItem);

            });
            expanderItems[groupID] = groupItem;
        });
        return expanderItems;
    },


    createGroupWidget: function (groupItem) {
        var self = this, o = this.options;
        var groupWidget = BI.createWidget({
            type: "bi.business_package_expander",
            nodeType: o.nodeType,
            validationChecker: function (v) {
                return BI.some(self.groupMap, function (groupID, obj) {
                    if (obj.name != v || groupItem.id === groupID) {
                        return true
                    }
                });
            },
            title: o.title,
            value: o.value,
            disableAdd: o.disableAdd,
            deleteInvisible: o.deleteInvisible,
            id: groupItem.id,
            forceNotSelected: o.forceNotSelected
        });
        groupWidget.populate(groupItem);
        this.groupWidgetMap[groupItem.id] = groupWidget;

        groupWidget.on(BI.BusinessPackageExpander.EVENT_CHANGE, function (obj) {
            var id = obj.attr("id");
            var packID = obj.attr("value");
            if (obj.isSelected() === true) {
                self.chosenFieldMap[id] = self.fieldMap[id];
            } else {
                delete self.chosenFieldMap[id];
            }
            var groupID = self._getGroupID(id);
            //var groupName = self.groupMap[groupID];
            self.fireEvent(BI.BusinessPackageGroupPane.EVENT_CHANGE, groupID, packID);
        });

        groupWidget.on(BI.BusinessPackageExpander.EVENT_CLICK_DELETE, function () {
            var id = groupWidget.attr("id");
            BI.Msg.confirm("", BI.i18nText("BI-Is_Dissolve_Group") + ":" + groupWidget.getNodeValue() + "?", function (v) {
                if (v === true) {
                    self.deleteGroupWidget(id);
                    self.fireEvent(BI.BusinessPackageGroupPane.EVENT_CLICK_DELETE)
                }
            });

        });

        groupWidget.on(BI.BusinessPackageExpander.EVENT_NODE_VALUE_CHANGE, function () {
            self.groupMap[groupWidget.attr("id")].name = groupWidget.getNodeValue();
            self.fireEvent(BI.BusinessPackageGroupPane.EVENT_GROUP_NAME_CHANGE);
        });

        groupWidget.on(BI.BusinessPackageExpander.EVENT_NODE_VALUE_CONFIRM, function () {
            self.groupMap[groupWidget.attr("id")].name = groupWidget.getNodeValue();
            self.fireEvent(BI.BusinessPackageGroupPane.EVENT_GROUP_NAME_CONFIRM);
        });


        groupWidget.on(BI.BusinessPackageExpander.EVENT_CLICK_ADD, function () {
            self.fireEvent(BI.BusinessPackageGroupPane.EVENT_CLICK_ADD, {
                gid: groupWidget.attr("id"),
                groupName: groupWidget.getNodeValue()
            })
        });

        groupWidget.on(BI.BusinessPackageExpander.EVENT_PACKAGE_DELETE, function (obj) {
            self.fireEvent(BI.BusinessPackageGroupPane.EVENT_PACKAGE_DELETE, obj)
        });

        groupWidget.on(BI.BusinessPackageExpander.EVENT_EDITOR_CONFIRM, function (packageName, packageID) {

            self.fireEvent(BI.BusinessPackageGroupPane.EVENT_EDITOR_CONFIRM, packageName, packageID)
        });

        return groupWidget;
    },

    empty: function () {
        this.groupMap = {};
        this.fieldMap = {};
        this.groupWidgetMap = {};
        this.chosenFieldMap = {};
        this.fieldInGroupMap = {};
        this.groupOfFieldMap = {};
        this.fieldPane.empty();
    },

    populate: function (items) {
        var self = this;
        this.empty();
        this._initFields(items);
        var fieldExpanderItems = self.createItemFromFieldMap(self.fieldMap);
        var emptyExpanderItems = self.createItemFromEmptyGroupMap();
        var expanderItems = BI.union(fieldExpanderItems, emptyExpanderItems);
        var groupWidgets = [];
        BI.each(expanderItems, function (i, expanderItem) {
            groupWidgets.push(self.createGroupWidget(expanderItem));
        });
        this.fieldPane.populate(groupWidgets);
        BI.each(self.fieldMap, function (fieldID, fieldName) {
            self.checkDuplicate(fieldID);
        });
    },


    setFieldSelectedTrue: function (fieldID) {
        var self = this;
        var groupID = self._getGroupID(fieldID);
        if (BI.isNotNull(self.groupWidgetMap[groupID])) {
            self.groupWidgetMap[groupID].setFieldSelectedTrue([fieldID]);
            self.chosenFieldMap[fieldID] = self.fieldMap[fieldID];
        }

    },

    setFieldSelectedFalse: function (fieldID) {
        var self = this;
        var groupID = self._getGroupID(fieldID);
        if (BI.isNotNull(self.groupWidgetMap[groupID])) {
            self.groupWidgetMap[groupID].setFieldSelectedFalse([fieldID]);
            delete self.chosenFieldMap[fieldID];
        }

    },

    deleteGroupWidget: function (groupID) {
        var self = this;
        BI.each(self.fieldInGroupMap[groupID], function (fieldID, fieldName) {
            delete self.groupOfFieldMap[self._getFieldID(fieldID)][groupID];
            var ID = self._createGroupFieldID(groupID, self._getFieldID(fieldID));
            self.deleteFieldWidget(ID, true);
        });
        this.groupWidgetMap[groupID].destroy();
        delete self.groupMap[groupID];
        delete self.groupWidgetMap[groupID];
        delete self.fieldInGroupMap[groupID];
    },

    addGroupWidget: function (groupName) {
        var self = this;
        var findGroup = BI.find(self.groupMap, function (groupID, obj) {
            if (groupName === obj.name) {
                return true;
            }
        });
        if (!findGroup) {
            var groupID = BI.UUID();
            var groupItem = {};
            groupItem.value = groupName;
            groupItem.id = groupID;
            self.groupMap[groupID] = {name: groupName, init_time: new Date().getTime()};
            self.fieldInGroupMap[groupID] = {};
            var expander = self.createGroupWidget(groupItem);
            self.fieldPane.addItem(expander);
        }
    },

    addFieldWidget: function (widgetID, fieldName, groupName) {
        var self = this;
        var groupItem = {};
        var groupID = BI.findKey(self.groupMap, function (id, obj) {
            return obj.name === groupName;
        });
        if (BI.isNotNull(groupID)) {
            groupItem.id = groupID;
            groupItem.value = groupName;
            groupItem.children = [];
            if (self._getGroupID(widgetID) != groupID) {
                var fieldItem = {};
                var fieldID = self._createGroupFieldID(groupID, self._getFieldID(widgetID));
                if (!BI.isNotNull(self.groupOfFieldMap[self._getFieldID(widgetID)])) {
                    self.fieldMap[fieldID] = fieldName;
                    self.groupOfFieldMap[self._getFieldID(widgetID)] = {};
                }
                fieldItem.id = fieldID;
                fieldItem.value = fieldName;
                groupItem.children.push(fieldItem);
                self.fieldInGroupMap[groupID][fieldID] = fieldName;
                self.groupOfFieldMap[self._getFieldID(fieldID)][groupID] = groupName;
                self.fieldMap[fieldID] = fieldName;
            }
            var groupWidget = self.groupWidgetMap[groupID];
            groupWidget.addFieldWidget(groupItem);
        }
        self.checkDuplicate(widgetID);
    },


    deleteFieldWidget: function (fieldID, checkEmpty) {
        var self = this;
        var groupID = self._getGroupID(fieldID);
        var field_ID = self._getFieldID(fieldID);
        if (BI.isNotNull(self.groupWidgetMap[groupID])) {
            self.groupWidgetMap[groupID].deleteFieldWidget(fieldID);
            delete self.fieldInGroupMap[groupID][fieldID];
            delete self.groupOfFieldMap[self._getFieldID(fieldID)][groupID];
            delete self.chosenFieldMap[fieldID];
            self.checkDuplicate(fieldID);
            if (BI.isNotNull(checkEmpty) && BI.size(self.groupOfFieldMap[field_ID]) === 0) {
                self.fireEvent(BI.BusinessPackageGroupPane.EVENT_EMPTY_GROUP, fieldID, self.fieldMap[fieldID])
            }

            delete self.fieldMap[fieldID];
        }
    },

    doRedMark: function (keyword, fieldID) {
        var self = this;
        var groupID = this._getGroupID(fieldID);
        if (BI.isNotNull(self.groupWidgetMap[groupID])) {
            self.groupWidgetMap[groupID].doRedMark(keyword, fieldID);
        }
    }
    ,

    doHighLight: function (fieldID) {
        var self = this;
        var groupID = this._getGroupID(fieldID);
        if (BI.isNotNull(self.groupWidgetMap[groupID])) {
            self.groupWidgetMap[groupID].doHighLight(fieldID);
        }

    }
    ,

    setFieldRightValue: function (value, fieldID) {
        var self = this;
        var groupID = this._getGroupID(fieldID);
        if (BI.isNotNull(self.groupWidgetMap[groupID])) {
            self.groupWidgetMap[groupID].setFieldRightValue(value, fieldID);
        }

    },

    getSelectedFieldMap: function () {
        return this.chosenFieldMap;
    }
    ,

    getFieldMap: function () {
        return this.fieldMap;
    },

    getGroupMap: function () {
        return this.groupMap;
    }
    ,


    checkSelectedAll: function () {
        var self = this;
        if (BI.size(self.chosenFieldMap) === 0) {
            return "none"
        } else if (BI.size(self.chosenFieldMap) === BI.size(self.fieldMap)) {
            return "all"
        } else {
            return "half"
        }
    }
    ,

    checkDuplicate: function (fieldID) {
        var self = this, o = this.options;
        if (o.enableCheckGroup === true) {
            var ID = self._getFieldID(fieldID);
            var groupMap = self.groupOfFieldMap[ID];
            if (BI.size(groupMap) > 1) {
                BI.each(groupMap, function (groupID, groupName) {
                    var field_ID = self._createGroupFieldID(groupID, ID);
                    self.groupWidgetMap[groupID].doHighLight(field_ID);
                })
            } else {
                var field_ID = self._getFieldID(fieldID);
                BI.each(groupMap, function (groupID, groupName) {
                    var ID = self._createGroupFieldID(groupID, field_ID);
                    self.groupWidgetMap[groupID].unHighLight(ID);
                });
            }
        }

    },

    setGroupName: function (newName, oldName) {
        var self = this;
        var groupID = BI.findKey(self.groupMap, function (groupID, obj) {
            return obj.name === oldName;
        });
        self.groupMap[groupID].name = newName;
    }


});
BI.BusinessPackageGroupPane.EVENT_CHANGE = "EVENT_CHANGE";
BI.BusinessPackageGroupPane.EVENT_EMPTY_GROUP = "EVENT_EMPTY_GROUP";
BI.BusinessPackageGroupPane.EVENT_PACKAGE_DELETE = "EVENT_PACKAGE_DELETE";
BI.BusinessPackageGroupPane.EVENT_CLICK_DELETE = "EVENT_CLICK_DELETE";
BI.BusinessPackageGroupPane.EVENT_CLICK_ADD = "EVENT_CLICK_ADD";
BI.BusinessPackageGroupPane.EVENT_GROUP_NAME_CHANGE = "EVENT_GROUP_NAME_CHANGE";
BI.BusinessPackageGroupPane.EVENT_GROUP_NAME_CONFIRM = "EVENT_GROUP_NAME_CONFIRM";
BI.BusinessPackageGroupPane.EVENT_PACKAGE_NAME_CHANGE = "EVENT_PACKAGE_NAME_CHANGE";
BI.BusinessPackageGroupPane.EVENT_EDITOR_CONFIRM = "EVENT_EDITOR_CONFIRM";
$.shortcut("bi.business_package_group_pane", BI.BusinessPackageGroupPane);