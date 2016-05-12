/**
 * Created by roy on 15/10/30.
 */
BI.CustomgroupGroupExpander = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomgroupGroupExpander.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-group-expander",
            id: "",
            item: {},
            title: "",
            nodeType: "bi.arrow_group_node_delete"

        })
    },

    _init: function () {
        this.fieldWidgetMap = {};
        var self = this, o = this.options;
        BI.CustomgroupGroupExpander.superclass._init.apply(this, arguments);
        var popupButtons = this._createFieldButtons(o.item);

        this.popup = BI.createWidget({
            type: "bi.left",
            items: popupButtons
        });

        this.node = BI.createWidget({
            type: o.nodeType,
            validationChecker: function (v) {
                return o.validationChecker(v, self.attr("id"))
            },
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
            self.fireEvent(BI.CustomgroupGroupExpander.EVENT_CLICK_DELETE);
        });

        this.node.on(BI.ArrowNodeDelete.EVENT_CHANGE, function () {
            self.fireEvent(BI.CustomgroupGroupExpander.EVENT_NODE_VALUE_CHANGE);
        });

        this.node.on(BI.ArrowNodeDelete.EVENT_CONFIRM, function () {
            self.fireEvent(BI.CustomgroupGroupExpander.EVENT_NODE_VALUE_CONFIRM)
        });


        self.checkFieldEmpty();

    },

    _createFieldButtons: function (item) {
        var self = this, popupButtons = [], o = this.options;
        if (BI.isNotNull(item.content) && BI.isNotEmptyArray(item.content)) {
            BI.each(item.content, function (i_in, item_in) {
                var findField = BI.find(self.fieldWidgetMap, function (fieldId, fieldWidget) {
                    if (item_in.id === fieldId) {
                        return true;
                    }
                });

                if (!findField) {
                    var fieldButton = BI.createWidget({
                        type: "bi.custom_group_field_button",
                        valueLeft: item_in.value,
                        id: item_in.id,
                        title: o.title,
                        cls: "item-custom-group",
                        textHeight:25,
                        hgap: 10,
                    });
                    self.fieldWidgetMap[item_in.id] = fieldButton;
                    fieldButton.on(BI.CustomGroupFieldButton.EVENT_CHANGE, function (value, obj) {
                        self.fireEvent(BI.CustomgroupGroupExpander.EVENT_CHANGE, obj)
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
        self.checkFieldEmpty();
    },

    deleteFieldWidget: function (widgetID) {
        var self = this;
        if (BI.isNotNull(self.fieldWidgetMap[widgetID])) {
            var fieldButton = self.fieldWidgetMap[widgetID];
            fieldButton.destroy();
            delete self.fieldWidgetMap[widgetID];
            self.checkFieldEmpty();
        }
    },

    populate: function (item) {
        var self = this;
        var popupButtons = self._createFieldButtons(item);
        self.node.setValue(item.value);
        self.popup.empty();
        self.popup.populate(popupButtons);
        self.checkFieldEmpty();
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

    checkFieldEmpty: function () {
        var self = this;
        if (BI.size(self.fieldWidgetMap) === 0) {
            self.emptyButton = BI.createWidget({
                type: "bi.text_button",
                value: BI.i18nText("BI-Null"),
                cls: "item-custom-group",
                hgap: 10,
                height: 25,
                disabled: true
            });
            self.popup.populate([self.emptyButton]);
        } else {
            if (BI.isNotNull(self.emptyButton)) {
                self.emptyButton.destroy();
            }

        }
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

BI.CustomgroupGroupExpander.EVENT_CHANGE = "EVENT_CHANGE";
BI.CustomgroupGroupExpander.EVENT_CLICK_DELETE = "EVENT_CLICK_DELETE";
BI.CustomgroupGroupExpander.EVENT_NODE_VALUE_CHANGE = "EVENT_NODE_VALUE_CHANGE";
BI.CustomgroupGroupExpander.EVENT_NODE_VALUE_CONFIRM = "EVENT_NODE_VALUE_CONFIRM";
$.shortcut("bi.custom_group_group_expander", BI.CustomgroupGroupExpander);