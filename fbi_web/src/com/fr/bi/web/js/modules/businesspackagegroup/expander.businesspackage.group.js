/**
 * Created by roy on 15/11/20.
 */
BI.BusinessPackageExpander = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.BusinessPackageExpander.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-business-package-group-expander",
            id: "",
            item: {},
            title: "",
            nodeType: "bi.arrow_group_node_delete",
            deleteInvisible: true,
            disableAdd: false,
            forceNotSelected: false

        })
    },

    _init: function () {
        this.fieldWidgetMap = {};
        var self = this, o = this.options;
        BI.BusinessPackageExpander.superclass._init.apply(this, arguments);
        var popupButtons = this._createFieldButtons(o.item);

        this.popup = BI.createWidget({
            type: "bi.left",
            items: popupButtons
        });

        this.node = BI.createWidget({
            type: o.nodeType,
            validationChecker: o.validationChecker,
            cls: "bi-custom-group-group-name",
            hoverClass: "search-close-h-font",
            height: 40,
            open: true,
            value: o.item.value
        });

        var expander = BI.createWidget({
            type: "bi.expander",
            element: this.element,
            id: o.item.id,
            isDefaultInit: true,
            el: self.node,
            popup: this.popup
        });


        this.node.on(BI.ArrowNodeDelete.EVENT_CLICK_DELETE, function () {
            self.fireEvent(BI.BusinessPackageExpander.EVENT_CLICK_DELETE);
        });

        this.node.on(BI.ArrowNodeDelete.EVENT_CHANGE, function () {
            self.fireEvent(BI.BusinessPackageExpander.EVENT_NODE_VALUE_CHANGE);
        });

        this.node.on(BI.ArrowNodeDelete.EVENT_CONFIRM, function () {
            self.fireEvent(BI.BusinessPackageExpander.EVENT_NODE_VALUE_CONFIRM);
        })

    },

    _createAddButton: function () {
        var self = this, o = this.options;
        var addButton = BI.createWidget({
            type: "bi.business_pack_add",
            disabled: o.disableAdd
        });
        addButton.on(BI.BusinessPackageAdd.EVENT_CHANGE, function () {
            self.fireEvent(BI.BusinessPackageExpander.EVENT_CLICK_ADD);
        });
        return addButton;
    },
    _createFieldButtons: function (item) {
        var self = this, popupButtons = [], o = this.options;
        if (BI.isNotNull(item.children) && BI.isNotEmptyArray(item.children)) {
            BI.each(item.children, function (i_in, item_in) {
                var findField = BI.find(self.fieldWidgetMap, function (fieldId, fieldWidget) {
                    if (item_in.id === fieldId) {
                        return true;
                    }
                });

                if (!findField) {
                    var tables = BI.Utils.getConfPackageTablesByID(item_in.value);
                    var packageName = BI.Utils.getConfPackageNameByID(item_in.value);
                    var fieldButton = BI.createWidget({
                        type: "bi.business_package_button",
                        text: packageName,
                        value: item_in.value,
                        table_count: tables.length,
                        id: item_in.id,
                        deleteInvisible: o.deleteInvisible,
                        forceNotSelected: o.forceNotSelected
                    });
                    self.fieldWidgetMap[item_in.id] = fieldButton;
                    fieldButton.on(BI.Controller.EVENT_CHANGE, function (type, value, obj) {
                        self.fireEvent(BI.BusinessPackageExpander.EVENT_CHANGE, obj);
                    });
                    fieldButton.on(BI.BusinessPackageButton.EVENT_CLICK_DELETE, function (obj) {
                        self.fireEvent(BI.BusinessPackageExpander.EVENT_PACKAGE_DELETE, obj)
                    });
                    fieldButton.on(BI.BusinessPackageButton.EVENT_EDITOR_CONFIRM, function (packageName, packageID) {
                        self.fireEvent(BI.BusinessPackageExpander.EVENT_EDITOR_CONFIRM, packageName, packageID)
                    });
                    popupButtons.push(fieldButton);
                }
            })
        }
        return popupButtons;
    },


    addFieldWidget: function (item) {
        var self = this;
        var buttons = self._createFieldButtons(item);
        self.popup.addItems(buttons);
    },

    deleteFieldWidget: function (widgetID) {
        var self = this;
        if (BI.isNotNull(self.fieldWidgetMap[widgetID])) {
            var fieldButton = self.fieldWidgetMap[widgetID];
            fieldButton.destroy();
            delete self.fieldWidgetMap[widgetID];
        }
    },


    populate: function (item) {
        var self = this;
        var addButton = self._createAddButton();
        var popupButtons = self._createFieldButtons(item);
        self.node.setValue(item.value);
        self.popup.empty();
        self.popup.populate(BI.union([addButton], popupButtons));
    },

    getNodeValue: function () {
        return this.node.getValue();
    },

    setFieldSelectedTrue: function (widgetIDs) {
        var self = this;
        BI.each(widgetIDs, function (i, fieldID) {
            if (BI.isNotNull(self.fieldWidgetMap[fieldID])) {
                self.fieldWidgetMap[fieldID].setSelected(true);
            }
        })
    },

    setFieldSelectedFalse: function (widgetIDs) {
        var self = this;
        BI.each(widgetIDs, function (i, fieldID) {
            if (BI.isNotNull(self.fieldWidgetMap[fieldID])) {
                self.fieldWidgetMap[fieldID].setSelected(false);
            }
        })
    },


    doRedMark: function (keyword, fieldID) {
        var self = this;
        if (BI.isNotNull(self.fieldWidgetMap[fieldID])) {
            self.fieldWidgetMap[fieldID].doRedMark(keyword);
        }
    },

    doHighLight: function (fieldID) {
        this.fieldWidgetMap[fieldID].doHighLight();
    },

    unHighLight: function (fieldID) {
        this.fieldWidgetMap[fieldID].unHighLight();
    },

    setFieldRightValue: function (value, fieldID) {
        this.fieldWidgetMap[fieldID].setValueRight(value);
    }
});

BI.BusinessPackageExpander.EVENT_CHANGE = "EVENT_CHANGE";
BI.BusinessPackageExpander.EVENT_CLICK_DELETE = "EVENT_CLICK_DELETE";
BI.BusinessPackageExpander.EVENT_PACKAGE_DELETE = "EVENT_PACKAGE_DELETE";
BI.BusinessPackageExpander.EVENT_NODE_VALUE_CHANGE = "EVENT_NODE_VALUE_CHANGE";
BI.BusinessPackageExpander.EVENT_NODE_VALUE_CONFIRM = "EVENT_NODE_VALUE_CONFIRM";
BI.BusinessPackageExpander.EVENT_CLICK_ADD = "EVENT_CLICK_ADD";
BI.BusinessPackageExpander.EVENT_EDITOR_CONFIRM = "EVENT_EDITOR_CONFIRM";
$.shortcut("bi.business_package_expander", BI.BusinessPackageExpander);