/**
 * Created by roy on 15/11/20.
 */
BI.BusinessPackageGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.BusinessPackageGroup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-business-package-group"
        })
    },
    _init: function () {
        var self = this;
        BI.BusinessPackageGroup.superclass._init.apply(this, arguments);
        this.chosenLabel = BI.createWidget({
            type: "bi.label",
            textHeight: 30,
            value: BI.i18nText("BI-Have_Selected") + 0 + BI.i18nText("BI-Item")
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

        this.saveButton = BI.createWidget({
            type: "bi.button",
            value: BI.i18nText("BI-Save"),
            height: 30
        });

        this.cancelButton = BI.createWidget({
            type: "bi.button",
            value: BI.i18nText("BI-Cancel"),
            height: 30
        });


        var top = BI.createWidget({
            type: "bi.right",
            hgap: 10,
            items: [
                self.saveButton,
                self.cancelButton,
                self.removeFieldButton,
                self.copy2group,
                self.move2group,
                self.chosenLabel
            ]
        });

        this.groupPane = BI.createWidget({
            type: "bi.business_package_ungroup_and_group_pane",
            enableCheckGroup: true,
            disableAdd: true,
            deleteInvisible: true
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [
                {
                    height: 40,
                    el: top
                }, {
                    el: self.groupPane
                }


            ]
        });

        this.move2group.on(BI.Move2GroupCombo.EVENT_CONFIRM, function () {
            var groupName = self.move2group.getValue();
            self._move2groupHandle(groupName);

        });

        this.move2group.on(BI.Move2GroupCombo.EVENT_CLICK_NEW_BUTTON, function () {
            var groupName = self.move2group.getTargetValue();
            self.groupPane.addGroupWidget(groupName);
            self._move2groupHandle(groupName);
            self._scrollToBottom();
        });

        this.move2group.on(BI.Move2GroupCombo.EVENT_BEFORE_POPUPVIEW, function () {
            var groupMap = self.groupPane.getGroupMap();
            var groupItems = self.groupPane.createItemFromGroupedFieldMap(groupMap);
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
            self.groupPane.addGroupWidget(groupName);
            var groupMap = self.groupPane.getGroupMap();
            var groupItems = self.groupPane.createItemFromGroupedFieldMap(groupMap);
            BI.each(groupItems, function (i, groupItem) {
                groupItem.title = groupItem.value;
                groupItem.text = groupItem.value;
            });
            self.copy2group.populate(groupItems);
            self.copy2group.setValue(groupName);
            self._scrollToBottom();
        });

        this.copy2group.on(BI.Copy2GroupCombo.EVENT_BEFORE_POPUPVIEW, function () {
            var groupMap = self.groupPane.getGroupMap();
            var groupItems = self.groupPane.createItemFromGroupedFieldMap(groupMap);
            BI.each(groupItems, function (i, groupItem) {
                groupItem.title = groupItem.value;
                groupItem.text = groupItem.value;
            });
            self.copy2group.populate(groupItems);
        });

        this.removeFieldButton.on(BI.Button.EVENT_CHANGE, function () {
            var chosenMap = self.groupPane.getSelectedFieldMap();
            BI.each(chosenMap, function (fieldID, fieldName) {
                self.groupPane.deleteFieldWidget(fieldID, true);
            });
            self._checkChosenNum();
        });

        this.saveButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.BusinessPackageGroup.EVENT_CHANGE);
        });

        this.cancelButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.BusinessPackageGroup.EVENT_CANCEL)
        });

        this.groupPane.on(BI.BusinessUngroupAndGroupPane.EVENT_CHANGE, function () {
            self._checkChosenNum();
        });


    },

    _getGroupID: function (id) {
        var ids = id.split("_");
        return ids[0];
    },

    _copy2groupHandle: function (groupName) {
        var self = this;
        var chosenMap = self.groupPane.getSelectedFieldMap();
        BI.each(chosenMap, function (id, fieldName) {
            self.groupPane.addFieldWidget(id, fieldName, groupName);
        });
        self._checkChosenNum();
    },

    _scrollToBottom: function () {
        var self = this;
        BI.delay(function () {
            self.groupPane.element.scrollTop(BI.MAX);
        }, 30);
    },

    _move2groupHandle: function (groupName) {
        var self = this;
        var unGroupedChosenMap = self.groupPane.getUnGroupedSelectedFieldMap();
        BI.each(unGroupedChosenMap, function (id, fieldName) {
            self.groupPane.addFieldWidget(id, fieldName, groupName);
            self.groupPane.deleteFieldWidget(id, true);
        });

        var groupedChosenMap = self.groupPane.getGroupedSelectedFieldMap();
        var groupMap = self.groupPane.getGroupMap();
        BI.each(groupedChosenMap, function (id, fieldName) {
            self.groupPane.addFieldWidget(id, fieldName, groupName);
            var groupID = self._getGroupID(id);
            if (groupName != groupMap[groupID]) {
                self.groupPane.deleteFieldWidget(id, true);
            }
        });
        self._checkChosenNum();
    },

    _checkChosenNum: function () {
        var self = this;
        var chosenMap = self.groupPane.getSelectedFieldMap();
        var chosenNum = BI.size(chosenMap);
        self.chosenLabel.setValue(BI.i18nText("BI-Have_Selected") + chosenNum + BI.i18nText("BI-Item"));
        self.chosenLabel.doRedMark(self.chosenLabel.getValue());
        if (chosenNum > 0) {
            self.move2group.setEnable(true);
            var ungroupedMap = self.groupPane.getUnGroupedSelectedFieldMap();
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
            self.copy2group.setEnable(false);
            self.copy2group.setTitle("");
            self.removeFieldButton.setEnable(false);
            self.copy2group.setTitle("");
        }


    },


    populate: function () {
        var self = this, o = this.options;
        var pgids = BI.Utils.getConfPackageGroupIDs();
        var pids = BI.Utils.getConfAllPackageIDs();
        var groupedFieldItems = [];
        var unGroupedFieldItem = {};
        unGroupedFieldItem.value = BI.i18nText("BI-Ungrouped_China");
        unGroupedFieldItem.children = [];
        var groupedFieldMap = {};
        BI.each(pgids, function (i, gid) {
            var item = {};
            item.value = BI.Utils.getConfGroupNameByGroupId(gid);
            item.children = [];
            item.id = gid;
            BI.each(BI.Utils.getConfGroupChildrenByGroupId(gid), function (i, packageObject) {
                var object = {};
                object.value = packageObject.id;
                item.children.push(object);
            });
            BI.each(BI.Utils.getConfGroupChildrenByGroupId(gid), function (i, packageObject) {
                groupedFieldMap[packageObject.id] = packageObject.id;
            });
            groupedFieldItems.push(item);
        });
        BI.each(pids, function (i, pid) {
            var id = pid;
            if (!BI.isNotNull(groupedFieldMap[id])) {
                var packageItem = {};
                packageItem.value = pid;
                unGroupedFieldItem.children.push(packageItem);
            }
        });
        self.groupPane.populate([unGroupedFieldItem], BI.sortBy(groupedFieldItems, "position"));
        self._checkChosenNum();


    },

    getValue: function () {
        var self = this;
        var result = self.groupPane.createItemFromGroupMap();
        return result;
    }

});
BI.BusinessPackageGroup.EVENT_CHANGE = "EVENT_CHANGE";
BI.BusinessPackageGroup.EVENT_CANCEL = "EVENT_CANCEL";
$.shortcut("bi.business_package_group", BI.BusinessPackageGroup);