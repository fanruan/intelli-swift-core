/**
 * Created by roy on 15/11/2.
 */
BI.CustomGroup = BI.inherit(BI.Widget, {
    _constant: {
        OTHER_GROUP_EN: "other"


    },
    _defaultConfig: function () {
        return BI.extend(BI.CustomGroup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-group",
            did: ""
        })
    },
    _init: function () {
        BI.CustomGroup.superclass._init.apply(this, arguments);
        var self = this;
        this.chosenPane = BI.createWidget({
            type: "bi.custom_group_field_pane",
            title: BI.i18nText("BI-Click_Cancel_Selection"),
            nodeType: "bi.arrow_group_node",
            height: 350

        });

        this.combo = BI.createWidget({
            type: "bi.custom_group_combo",
            popup: self.chosenPane
        });


        this.move2group = BI.createWidget({
            type: "bi.move2group_combo",
            items: self.groupedItems,
            disabled: true
        });

        this.copy2group = BI.createWidget({
            type: "bi.copy2group_combo",
            items: self.groupedItems,
            disabled: true
        });

        this.removeFieldButton = BI.createWidget({
            type: 'bi.button',
            text: BI.i18nText("BI-Shift_Out_Group"),
            level: 'warning',
            tipType: 'warning',
            disabled: true,
            height: 30
        });


        this.fieldPane = BI.createWidget({
            type: "bi.custom_group_all_fields_pane",
            enableCheckGroup: true,
            height: 270,
            unGroupedItems: self.unGroupedItems
        });

        this.groupButtons = BI.createWidget({
            type: "bi.right",
            hgap: 5,
            items: [
                self.removeFieldButton,
                self.copy2group,
                self.move2group
            ]
        });

        this.buttons = BI.createWidget({
            type: "bi.center_adapt",
            items: [
                self.combo,
                self.groupButtons
            ]
        });


        this.searchPane = BI.createWidget({
            type: "bi.custom_group_searcher_pane",
            cls: "bi-custom-group-searchpane",
            nodeType: "bi.arrow_group_node",
            enableCheckGroup: true
        });

        this.search = BI.createWidget({
            type: "bi.custom_group_search",
            adapter: self.fieldPane,
            popup: self.searchPane,
            onSearch: function (op, callback) {
                var groupedFieldMap = self.fieldPane.getGroupedFieldMap();
                var ungroupedFieldMap = self.fieldPane.getUngroupedFieldMap();

                var res = BI.Func.getSearchResult(groupedFieldMap, op.keyword);
                groupedFieldMap = BI.concat(res.matched, res.finded);

                res = BI.Func.getSearchResult(ungroupedFieldMap, op.keyword);
                ungroupedFieldMap = BI.concat(res.matched, res.finded);

                var searchGroupedItem = self.fieldPane.createItemFromGroupedFieldMap(groupedFieldMap);
                var searchUngoupedItem = self.fieldPane.createItemFromUngroupedFieldMap(ungroupedFieldMap);
                var searchItem = BI.union(searchUngoupedItem, searchGroupedItem);
                var selectedIDs = [];
                var selectedFieldMap = self.fieldPane.getSelectedFieldMap();
                BI.each(selectedFieldMap, function (id, fieldName) {
                    selectedIDs.push(id);

                });

                callback(searchItem, op.keyword, selectedIDs);

            }
        });


        this.bottom = BI.createWidget({
            type: "bi.custom_group_group2other",
            validationChecker: function (v) {
                var result = BI.findKey(self.fieldPane.getGroupMap(), function (groupID, groupName) {
                    return groupName === v
                });
                if (BI.isNotNull(result)) {
                    return false
                } else {
                    return true
                }
            }
        });


        this.top = BI.createWidget({
            type: "bi.vertical",
            cls: "bi-custom-group-top",
            vgap: 10,
            items: [
                self.buttons,
                self.search
            ]
        });


        BI.createWidget({
            type: "bi.vertical",
            height: 390,
            element: self.element,
            items: [
                self.top,
                self.fieldPane,
                self.bottom
            ]
        });


        this.fieldPane.on(BI.CustomGroupAllFieldsPane.EVENT_CHANGE, function () {
            self._checkChosenNum();
        });

        this.searchPane.on(BI.CustomGroupSearcherPane.EVENT_TOOLBAR_VALUE_CHANGE, function (isSelected, fieldMap) {
            if (isSelected === true) {
                BI.each(fieldMap, function (id, value) {
                    self.fieldPane.setFieldSelectedTrue(id);
                })
            } else {
                BI.each(fieldMap, function (id, value) {
                    self.fieldPane.setFieldSelectedFalse(id);
                })
            }
            self._checkChosenNum();
        });

        this.searchPane.on(BI.CustomGroupSearcherPane.EVENT_CHANGE, function (obj) {
            if (obj.isSelected() === true) {
                self.fieldPane.setFieldSelectedTrue(obj.options.id);
            } else {
                self.fieldPane.setFieldSelectedFalse(obj.options.id);
            }
            self._checkChosenNum();
        });

        this.chosenPane.on(BI.CustomGroupFieldPane.EVENT_CHANGE, function (obj) {
            self.fieldPane.setFieldSelectedFalse(obj.options.id);
            self.chosenPane.deleteFieldWidget(obj.options.id);
            self._checkChosenNum();
        });

        this.combo.on(BI.CustomGroupCombo.EVENT_BEFORE_POPUPVIEW, function () {
            self.search.stopSearch();
            var groupedFieldMap = self.fieldPane.getGroupedSelectedFieldMap();
            var ungroupedFieldMap = self.fieldPane.getUnGroupedSelectedFieldMap();
            var groupedItem = self.fieldPane.createItemFromGroupedFieldMap(groupedFieldMap);
            var ungroupedItem = self.fieldPane.createItemFromUngroupedFieldMap(ungroupedFieldMap);
            var chosenItem = BI.union(ungroupedItem, groupedItem);
            self.chosenPane.populate(chosenItem);
            self._checkChosenNum();
        });

        this.move2group.on(BI.Move2GroupCombo.EVENT_CONFIRM, function () {
            var groupName = self.move2group.getValue();
            self._move2groupHandle(groupName);

        });

        this.move2group.on(BI.Move2GroupCombo.EVENT_CLICK_NEW_BUTTON, function () {
            var groupName = self.move2group.getTargetValue();
            var ungroupedSelectedFieldMap = self.fieldPane.getUnGroupedSelectedFieldMap();
            var ungroupedFieldMap = self.fieldPane.getUngroupedFieldMap();
            var groupMap = self.fieldPane.getGroupMap();
            var groups = {};
            var ungroupedFields = {};
            var duplicatedName = "";
            var isduplicated = false;
            BI.each(ungroupedSelectedFieldMap, function (id, fieldName) {
                delete ungroupedFieldMap[id];
            });
            BI.each(ungroupedFieldMap, function (id, fieldName) {
                ungroupedFields[fieldName] = id;
            });
            BI.each(groupMap, function (id, groupName) {
                groups[groupName] = id;
            });
            groups[groupName] = BI.UUID();
            BI.find(groups, function (groupName, id) {
                if (BI.isNotNull(ungroupedFields[groupName])) {
                    isduplicated = true;
                    duplicatedName = groupName;
                    return true;
                }
            });

            var otherName = self.bottom.getValue();
            var otherDuplicate = false;
            if (otherName === groupName || groupName === self._constant.OTHER_GROUP_EN) {
                isduplicated = true;
                duplicatedName = groupName;
                otherDuplicate = true;
            }


            if (!isduplicated) {
                self.fieldPane.addGroupWidget(groupName);
                self._move2groupHandle(groupName);
                self._scrollToBottom();
            } else if (otherDuplicate === true) {
                BI.Msg.alert(BI.i18nText("BI-Failure_Toast"), "\"" + duplicatedName + "\"" + BI.i18nText("BI-Failute_Fieldname_Othername_Duplicate"));
            }
            else {
                BI.Msg.alert(BI.i18nText("BI-Failure_Toast"), "\"" + duplicatedName + "\"" + BI.i18nText("BI-Failure_Fieldname_Duplicate"));
            }
        });

        this.move2group.on(BI.Move2GroupCombo.EVENT_BEFORE_POPUPVIEW, function () {
            var groupMap = self.fieldPane.getGroupMap();
            var groupItems = self.fieldPane.createItemFromGroupedFieldMap(groupMap);
            BI.each(groupItems, function (i, groupItem) {
                groupItem.title = groupItem.value;
                groupItem.text = groupItem.value;
            });
            self.move2group.populate(groupItems);
        });

        this.copy2group.on(BI.Copy2GroupCombo.EVENT_CONFIRM, function () {
            var groupNames = self.copy2group.getValue();
            BI.each(groupNames, function (i, groupName) {
                self._copy2groupHandle(groupName);
            })
        });

        this.copy2group.on(BI.Copy2GroupCombo.EVENT_CLICK_BUTTON, function () {
            var groupName = self.copy2group.getTargetValue();
            var ungroupedFieldMap = self.fieldPane.getUngroupedFieldMap();
            var groupMap = self.fieldPane.getGroupMap();
            var groups = {};
            var ungroupedFields = {};
            var isduplicated = false;
            var duplicatedName = "";
            BI.each(ungroupedFieldMap, function (id, fieldName) {
                ungroupedFields[fieldName] = id;
            });
            BI.each(groupMap, function (id, groupName) {
                groups[groupName] = id;
            });
            groups[groupName] = BI.UUID();
            BI.find(groups, function (groupName, id) {
                if (BI.isNotNull(ungroupedFields[groupName])) {
                    isduplicated = true;
                    duplicatedName = groupName;
                    return true;
                }
            });

            var otherName = self.bottom.getValue();
            var otherDuplicate = false;
            if (otherName === groupName || groupName === self._constant.OTHER_GROUP_EN) {
                duplicatedName = groupName;
                isduplicated = true;
                otherDuplicate = true;
            }

            if (!isduplicated) {
                self.fieldPane.addGroupWidget(groupName);
                var groupMap = self.fieldPane.getGroupMap();
                var groupItems = self.fieldPane.createItemFromGroupedFieldMap(groupMap);
                BI.each(groupItems, function (i, groupItem) {
                    groupItem.title = groupItem.value;
                    groupItem.text = groupItem.value;
                });
                self.copy2group.populate(groupItems);
                self.copy2group.setValue(groupName);
                self._scrollToBottom();
            } else if (otherDuplicate === true) {
                BI.Msg.alert(BI.i18nText("BI-Failure_Toast"), "\"" + duplicatedName + "\"" + BI.i18nText("BI-Failute_Fieldname_Othername_Duplicate"));
            }
            else {
                BI.Msg.alert(BI.i18nText("BI-Failure_Toast"), "\"" + duplicatedName + "\"" + BI.i18nText("BI-Failure_Fieldname_Duplicate"));
            }
        });

        this.copy2group.on(BI.Copy2GroupCombo.EVENT_BEFORE_POPUPVIEW, function () {
            var groupMap = self.fieldPane.getGroupMap();
            var groupItems = self.fieldPane.createItemFromGroupedFieldMap(groupMap);
            BI.each(groupItems, function (i, groupItem) {
                groupItem.title = groupItem.value;
                groupItem.text = groupItem.value;
            });
            self.copy2group.populate(groupItems);
        });

        this.removeFieldButton.on(BI.Button.EVENT_CHANGE, function () {
            var chosenMap = self.fieldPane.getGroupedSelectedFieldMap();
            var ungroupedFieldMap = self.fieldPane.getUngroupedFieldMap();
            var groupOfFieldMap = self.fieldPane.getGroupOfFieldMap();
            var groupMap = self.fieldPane.getGroupMap();
            var ungroupedFields = {};
            var groups = {};
            var duplicateFields = {};
            var isduplicated = false;
            BI.each(ungroupedFieldMap, function (id, fieldName) {
                ungroupedFields[fieldName] = id;
            });

            BI.each(groupMap, function (id, groupName) {
                groups[groupName] = id;
            });

            BI.each(chosenMap, function (ID, fieldName) {
                var groupID = self._getGroupID(ID);
                var fieldID = self._getFieldID(ID);
                delete groupOfFieldMap[fieldID][groupID];
                if (BI.size(groupOfFieldMap[fieldID]) === 0) {
                    ungroupedFields[fieldName] = BI.UUID();
                }
            });

            BI.each(groups, function (groupName, id) {
                if (BI.isNotNull(ungroupedFields[groupName])) {
                    duplicateFields[groupName] = "duplicateField";
                    isduplicated = true;
                }
            });

            if (!isduplicated) {
                self.search.stopSearch();
                BI.each(chosenMap, function (fieldID, fieldName) {
                    self.fieldPane.deleteFieldWidget(fieldID);
                });
                self._checkChosenNum();
            } else {
                var toast = "";
                BI.each(duplicateFields, function (fieldName, id) {
                    if (BI.isNotEmptyString(toast)) {
                        toast = toast + "、" + "\"" + fieldName + "\"";
                    } else {
                        toast = toast + "\"" + fieldName + "\"";
                    }
                });
                BI.Msg.alert(BI.i18nText("BI-Failure_Toast"), toast + BI.i18nText("BI-Failure_Groupname_Duplicate"));
            }

        });


    },

    _getGroupID: function (id) {
        var ids = id.split("_");
        return ids[0];
    },

    _getFieldID: function (id) {
        var ids = id.split("_");
        return ids[1];
    },

    _checkChosenNum: function () {
        var self = this;
        var chosenMap = self.fieldPane.getSelectedFieldMap();
        var chosenNum = BI.size(chosenMap);
        self.combo.setTriggerValue(BI.i18nText("BI-Have_Selected") + chosenNum + BI.i18nText("BI-Item"));
        self.combo.doRedMark(chosenNum);
        if (chosenNum > 0) {
            self.move2group.setEnable(true);
            self.move2group.setTitle("");
            var ungroupedMap = self.fieldPane.getUnGroupedSelectedFieldMap();
            if (BI.size(ungroupedMap) > 0) {
                self.copy2group.setEnable(false);
                self.copy2group.setTitle(BI.i18nText("BI-Ungrouped_Can_Move"));
                self.removeFieldButton.setEnable(false);
                self.removeFieldButton.setTitle(BI.i18nText("BI-Ungrouped_Can_Move"));
            } else {
                self.copy2group.setEnable(true);
                self.copy2group.setTitle("");
                self.removeFieldButton.setEnable(true);
                self.removeFieldButton.setTitle("");
            }
        } else {
            self.move2group.setEnable(false);
            self.move2group.setTitle(BI.i18nText("BI-Select_Null"));
            self.copy2group.setEnable(false);
            self.copy2group.setTitle(BI.i18nText("BI-Select_Null"));
            self.removeFieldButton.setEnable(false);
            self.removeFieldButton.setTitle("");
        }


    },

    _move2groupHandle: function (groupName) {
        var self = this;
        self.search.stopSearch();
        var unGroupedChosenMap = self.fieldPane.getUnGroupedSelectedFieldMap();
        BI.each(unGroupedChosenMap, function (id, fieldName) {
            self.fieldPane.addFieldWidget(id, fieldName, groupName);
            self.fieldPane.deleteFieldWidget(id);
        });

        var groupedChosenMap = self.fieldPane.getGroupedSelectedFieldMap();
        var groupMap = self.fieldPane.getGroupMap();
        BI.each(groupedChosenMap, function (id, fieldName) {
            self.fieldPane.addFieldWidget(id, fieldName, groupName);
            var groupID = self._getGroupID(id);
            if (groupName != groupMap[groupID]) {
                self.fieldPane.deleteFieldWidget(id);
            }
        });
        self._checkChosenNum();
    },

    _copy2groupHandle: function (groupName) {
        var self = this;
        self.search.stopSearch();
        var chosenMap = self.fieldPane.getSelectedFieldMap();
        BI.each(chosenMap, function (id, fieldName) {
            self.fieldPane.addFieldWidget(id, fieldName, groupName);
        });
        self._checkChosenNum();
    },

    _scrollToBottom: function () {
        var self = this;
        BI.delay(function () {
            self.fieldPane.element.scrollTop(BI.MAX);
        }, 30);
    },


    populate: function (groupedItems, ungroup2Other, ungroup2OtherName) {
        var self = this, o = this.options;
        var did = o.dId;
        self.bottom.populate(ungroup2Other, ungroup2OtherName);
        BI.Utils.getNoGroupedDataByDimensionID(did, function (unGroupedFields) {
            if (BI.size(unGroupedFields) > 1000) {
                if (!BI.Maskers.has(self.getName())) {
                    self._tooManyFieldsPane = BI.createWidget({
                        type: "bi.center_adapt",
                        cls: "bi-custom-group-disable-mask",
                        items: [
                            {
                                type: "bi.label",
                                cls: "mask-label",
                                value: BI.i18nText("BI-Unsupport_Too_Many_Fields"),
                                textHeight: 30
                            }
                        ],
                        element: BI.Maskers.make(self.getName(), self)
                    });
                }
                BI.Maskers.show(self.getName());
            } else {
                var unGroupedFieldItem = {};
                unGroupedFieldItem.value = BI.i18nText("BI-Ungrouped_China");
                unGroupedFieldItem.content = [];
                var groupedFieldMap = {};
                BI.each(groupedItems, function (i, groupItem) {
                    BI.each(groupItem.content, function (i, fieldItem) {
                        groupedFieldMap[fieldItem.value] = fieldItem.value;
                    })
                });

                BI.each(unGroupedFields, function (i, fieldName) {
                    if (!BI.isNotNull(groupedFieldMap[fieldName])) {
                        var fieldItem = {};
                        fieldItem.value = fieldName;
                        unGroupedFieldItem.content.push(fieldItem);
                    }
                });
                self.fieldPane.populate([unGroupedFieldItem], groupedItems);
                self._checkChosenNum();
            }


        });


    },


    getValue: function () {
        var self = this;
        var group = {};
        var sort = {};
        sort.details = [];
        var ungroupedName = self.bottom.getValue();
        group.details = self.fieldPane.createItemFromGroupMap();
        BI.each(group.details, function (i, groupobj) {
            sort.details.push(groupobj.value);
        })
        if (self.bottom.isSelected()) {
            group.ungroup2Other = 1;
            group.ungroup2OtherName = ungroupedName;
            sort.details.push(ungroupedName);
        } else {
            group.ungroup2Other = 0;
            group.ungroup2OtherName = "";
        }
        return group;
    }


});
$.shortcut("bi.custom_group", BI.CustomGroup);