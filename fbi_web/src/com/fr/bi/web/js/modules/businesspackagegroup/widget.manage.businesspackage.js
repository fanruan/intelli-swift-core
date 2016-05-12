/**
 * Created by roy on 15/11/25.
 */
BI.BusinessPackageManage = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.BusinessPackageManage.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-business-package-group"
        })
    },

    _init: function () {
        var self = this;
        BI.BusinessPackageManage.superclass._init.apply(this, arguments);
        var addButton = BI.createWidget({
            type: "bi.button",
            value: BI.i18nText("BI-Add_Grouping"),
            level: "ignore",
            height: 26
        });

        var groupButton = BI.createWidget({
            type: "bi.button",
            value: BI.i18nText("BI-Batch_Grouping"),
            level: "common",
            height: 26
        });


        var top = BI.createWidget({
            type: "bi.right",
            hgap: 10,
            items: [
                groupButton,
                addButton
            ]
        });

        this.groupPane = BI.createWidget({
            type: "bi.business_package_ungroup_and_group_pane",
            enableCheckGroup: true,
            forceNotSelected: true
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

        addButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.BusinessPackageManage.EVENT_GROUP_ADD);
            self._scrollToBottom();
        });

        groupButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.BusinessPackageManage.EVENT_BATCH_GROUP);
        });


        this.groupPane.on(BI.BusinessUngroupAndGroupPane.EVENT_CLICK_DELETE, function () {
            self.fireEvent(BI.BusinessPackageManage.EVENT_CLICK_DELETE);
        });

        this.groupPane.on(BI.BusinessUngroupAndGroupPane.EVENT_CLICK_ADD, function (groupObj) {
            var groupID = groupObj.gid || "";
            var groupName = groupObj.groupName;
            self.fireEvent(BI.BusinessPackageManage.EVENT_PACKAGE_ADD, groupID, groupName)
        });

        this.groupPane.on(BI.BusinessUngroupAndGroupPane.EVENT_CHANGE, function (groupID, packID) {
            self.fireEvent(BI.BusinessPackageManage.EVENT_CHANGE, groupID, packID)
        });

        this.groupPane.on(BI.BusinessUngroupAndGroupPane.EVENT_PACKAGE_DELETE, function (obj) {
            var packageID = obj.attr("value");
            self.fireEvent(BI.BusinessPackageManage.EVENT_PACKAGE_DELETE, packageID)
        });

        this.groupPane.on(BI.BusinessUngroupAndGroupPane.EVENT_GROUP_NAME_CHANGE, function () {
            self.fireEvent(BI.BusinessPackageManage.EVENT_GROUP_NAME_CHANGE)
        });

        this.groupPane.on(BI.BusinessUngroupAndGroupPane.EVENT_GROUP_NAME_CONFIRM, function () {
            self.fireEvent(BI.BusinessPackageManage.EVENT_GROUP_NAME_CONFIRM)
        });


        this.groupPane.on(BI.BusinessUngroupAndGroupPane.EVENT_EDITOR_CONFIRM, function (packageName, packageID) {
            self.fireEvent(BI.BusinessPackageManage.EVENT_EDITOR_CONFIRM, packageName, packageID)
        })

    },


    addGroupWidget: function (groupName) {
        this.groupPane.addGroupWidget(groupName);
    },

    addFieldWidget: function (widgetID, fieldName, groupName) {
        this.groupPane.addFieldWidget(widgetID, fieldName, groupName);
    },


    populate: function (groupedItems, allPackages) {
        var self = this, o = this.options;
        var groupedFieldItems = [];
        var unGroupedFieldItem = {};
        unGroupedFieldItem.value = BI.i18nText("BI-Ungrouped_China");
        unGroupedFieldItem.children = [];
        var groupedFieldMap = {};
        BI.each(groupedItems, function (i, groupItem) {
            var item = {};
            item.value = groupItem.name;
            item.children = [];
            item.id = groupItem.id;
            item.init_time = groupItem.init_time;
            BI.each(groupItem.children, function (i, packageObject) {
                var object = {};
                object.value = packageObject.id;
                item.children.push(object);
            });
            BI.each(groupItem.children, function (i, packageObject) {
                groupedFieldMap[packageObject.id] = packageObject.id;
            });
            groupedFieldItems.push(item);
        });
        BI.each(allPackages, function (i, packageObject) {
            var id = packageObject.id;
            if (!BI.isNotNull(groupedFieldMap[id])) {
                var packageItem = {};
                packageItem.value = packageObject.id;
                unGroupedFieldItem.children.push(packageItem);
            }
        });
        self.groupPane.populate([unGroupedFieldItem], groupedFieldItems);
    },

    getValue: function () {
        var self = this;
        var result = self.groupPane.createItemFromGroupMap();
        return result;
    },

    _scrollToBottom: function () {
        var self = this;
        BI.delay(function () {
            self.groupPane.element.scrollTop(BI.MAX);
        }, 30);
    },
});
BI.BusinessPackageManage.EVENT_CHANGE = "EVENT_CHANGE";
BI.BusinessPackageManage.EVENT_CLICK_DELETE = "EVENT_CLICK_DELETE";
BI.BusinessPackageManage.EVENT_PACKAGE_DELETE = "EVENT_PACKAGE_DELETE";
BI.BusinessPackageManage.EVENT_PACKAGE_ADD = "EVENT_PACKAGE_ADD";
BI.BusinessPackageManage.EVENT_GROUP_ADD = "EVENT_GROUP_ADD";
BI.BusinessPackageManage.EVENT_BATCH_GROUP = "EVENT_BATCH_GROUP";
BI.BusinessPackageManage.EVENT_GROUP_NAME_CHANGE = "EVENT_GROUP_NAME_CHANGE";
BI.BusinessPackageManage.EVENT_GROUP_NAME_CONFIRM = "EVENT_GROUP_NAME_CONFIRM";
BI.BusinessPackageManage.EVENT_EDITOR_CONFIRM = "EVENT_EDITOR_CONFIRM";
$.shortcut("bi.business_package_manage", BI.BusinessPackageManage);