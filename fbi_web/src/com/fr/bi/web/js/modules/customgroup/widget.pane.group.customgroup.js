/**
 * Created by roy on 15/10/29.
 */
BI.CustomGroupFieldPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomGroupFieldPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-group-field-pane",
            nodeType: "bi.arrow_group_node_delete",
            title: "",
            enableCheckGroup: false
        })
    },

    _init: function () {
        var self = this, o = this.options;
        BI.CustomGroupFieldPane.superclass._init.apply(this, arguments);
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
            self.groupMap[group_id] = item.value;
            self.fieldInGroupMap[group_id] = {};
            BI.each(item.content, function (i_in, item_in) {
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
                self.groupOfFieldMap[self._getFieldID(field_id)][group_id] = self.groupMap[group_id];
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
                groupItem.value = self.groupMap[groupID];
                groupItem.content = [];
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
                    item_in.content.push(fieldItem);
                    return true;
                }
            });

            if (!found) {
                var groupItem = {};
                groupItem.id = groupID;
                groupItem.value = self.groupMap[groupID];
                groupItem.content = [];
                var fieldItem = {};
                fieldItem.id = fieldID;
                fieldItem.value = fieldName;
                groupItem.content.push(fieldItem);
                expanderItems.push(groupItem);
            }
        });
        return expanderItems;
    },

    createItemFromGroupMap: function () {
        var self = this, expanderItems = [];
        BI.each(self.groupMap, function (groupID, groupName) {
            var groupItem = {};
            groupItem.value = groupName;
            groupItem.content = [];
            BI.each(self.fieldInGroupMap[groupID], function (fieldID, fieldName) {
                var fieldItem = {};
                fieldItem.value = fieldName;
                groupItem.content.push(fieldItem);
            })
            expanderItems.push(groupItem);
        })
        return expanderItems;
    },


    createGroupWidget: function (groupItem) {
        var self = this, o = this.options;
        var groupWidget = BI.createWidget({
            type: "bi.custom_group_group_expander",
            nodeType: o.nodeType,
            validationChecker: o.validationChecker,
            title: o.title,
            id: groupItem.id
        });
        groupWidget.populate(groupItem);
        this.groupWidgetMap[groupItem.id] = groupWidget;

        groupWidget.on(BI.CustomgroupGroupExpander.EVENT_CHANGE, function (obj) {
            var id = obj.attr("id");
            if (obj.isSelected() === true) {
                self.chosenFieldMap[id] = self.fieldMap[id];
            } else {
                delete self.chosenFieldMap[id];
            }
            self.fireEvent(BI.CustomGroupFieldPane.EVENT_CHANGE, obj);
        });

        groupWidget.on(BI.CustomgroupGroupExpander.EVENT_CLICK_DELETE, function () {
            var id = groupWidget.attr("id");
            BI.Msg.confirm("", BI.i18nText("BI-Is_Dissolve_Group") + ":" + groupWidget.getNodeValue() + "?", function (v) {
                if (v === true) {
                    self.deleteGroupWidget(id);
                }
            });
        });

        //groupWidget.on(BI.CustomgroupGroupExpander.EVENT_NODE_VALUE_CHANGE, function () {
        //    self.groupMap[groupWidget.attr("id")] = groupWidget.getNodeValue();
        //    var items = self.createItemFromGroupMap();
        //    self.populate(items);
        //});

        groupWidget.on(BI.CustomgroupGroupExpander.EVENT_NODE_VALUE_CONFIRM, function () {
            self.groupMap[groupWidget.attr("id")] = groupWidget.getNodeValue();
            var items = self.createItemFromGroupMap();
            self.populate(items);
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
            self.deleteFieldWidget(ID);
        });
        this.groupWidgetMap[groupID].destroy();
        delete self.groupMap[groupID];
        delete self.groupWidgetMap[groupID];
        delete self.fieldInGroupMap[groupID];
    },

    addGroupWidget: function (groupName) {
        var self = this;
        var findGroup = BI.find(self.groupMap, function (groupID, Name) {
            if (groupName === Name) {
                return true;
            }
        });
        if (!findGroup) {
            var groupID = BI.UUID();
            var groupItem = {};
            groupItem.value = groupName;
            groupItem.id = groupID;
            self.groupMap[groupID] = groupName;
            self.fieldInGroupMap[groupID] = {};
            var expander = self.createGroupWidget(groupItem);
            self.fieldPane.addItem(expander);
        }
    },

    addFieldWidget: function (widgetID, fieldName, groupName) {
        var self = this;
        var groupItem = {};


        var groupID = BI.findKey(self.groupMap, function (id, name) {
            return name === groupName;
        });
        if (BI.isNotNull(groupID)) {
            groupItem.id = groupID;
            groupItem.value = groupName;
            groupItem.content = [];
            if (self._getGroupID(widgetID) != groupID) {
                var fieldItem = {};
                var fieldID = self._createGroupFieldID(groupID, self._getFieldID(widgetID));
                if (!BI.isNotNull(self.groupOfFieldMap[self._getFieldID(widgetID)])) {
                    self.fieldMap[fieldID] = fieldName;
                    self.groupOfFieldMap[self._getFieldID(widgetID)] = {};
                }
                fieldItem.id = fieldID;
                fieldItem.value = fieldName;
                groupItem.content.push(fieldItem);
                self.fieldInGroupMap[groupID][fieldID] = fieldName;
                self.groupOfFieldMap[self._getFieldID(fieldID)][groupID] = groupName;
                self.fieldMap[fieldID] = fieldName;
            }
            var groupWidget = self.groupWidgetMap[groupID];
            groupWidget.addFieldWidget(groupItem);
        }
        self.checkDuplicate(widgetID);
    },

    deleteFieldWidget: function (fieldID) {
        var self = this;
        var groupID = self._getGroupID(fieldID);
        var field_ID = self._getFieldID(fieldID);
        if (BI.isNotNull(self.groupWidgetMap[groupID])) {
            self.groupWidgetMap[groupID].deleteFieldWidget(fieldID);
            delete self.fieldInGroupMap[groupID][fieldID];
            delete self.groupOfFieldMap[self._getFieldID(fieldID)][groupID];
            delete self.chosenFieldMap[fieldID];
            self.checkDuplicate(fieldID);
            if (BI.size(self.groupOfFieldMap[field_ID]) === 0) {
                self.fireEvent(BI.CustomGroupFieldPane.EVENT_EMPTY_GROUP, fieldID, self.fieldMap[fieldID])
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
                var duplicateText = "";
                BI.each(groupMap, function (groupID, groupName) {
                    if (BI.isEmptyString(duplicateText)) {
                        duplicateText = groupName
                    } else {
                        duplicateText = duplicateText + "、" + groupName;
                    }

                });

                BI.each(groupMap, function (groupID, groupName) {
                    var field_ID = self._createGroupFieldID(groupID, ID);
                    self.groupWidgetMap[groupID].setFieldRightValue("(" + duplicateText + ")", field_ID);
                    self.groupWidgetMap[groupID].doHighLight(field_ID);
                })
            } else {
                var field_ID = self._getFieldID(fieldID);
                BI.each(groupMap, function (groupID, groupName) {
                    var ID = self._createGroupFieldID(groupID, field_ID);
                    self.groupWidgetMap[groupID].setFieldRightValue("", ID);
                    self.groupWidgetMap[groupID].unHighLight(ID);
                });
            }
        }

    },

    setGroupName: function (newName, oldName) {
        var self = this;
        var groupID = BI.findKey(self.groupMap, function (groupID, groupName) {
            return groupName === oldName;
        });
        self.groupMap[groupID] = newName;
    },

    getGroupOfFieldMap: function () {
        return this.groupOfFieldMap;
    }


});
BI.CustomGroupFieldPane.EVENT_CHANGE = "EVENT_CHANGE";
BI.CustomGroupFieldPane.EVENT_EMPTY_GROUP = "EVENT_EMPTY_GROUP";
$.shortcut("bi.custom_group_field_pane", BI.CustomGroupFieldPane);