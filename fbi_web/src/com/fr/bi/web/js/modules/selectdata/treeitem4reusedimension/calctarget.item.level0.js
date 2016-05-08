/**
 * @class BI.DetailSelectCalculationTargetLevel0Item
 * @extends BI.Single
 */
BI.DetailSelectCalculationTargetLevel0Item = BI.inherit(BI.BasicButton, {

    constants: {
        NUMBER: 1,
        CALC: 0
    },

    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectCalculationTargetLevel0Item.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-calc-data-level0-item",
            trigger: "mousedown",
            hgap: 0,
            layer: 0,
            lgap: 0,
            rgap: 35
        })
    },

    _init: function () {
        BI.DetailSelectCalculationTargetLevel0Item.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var expression = o.value._src.expression;
        var targetIds = expression.ids;
        this.icons = [];
        var icon = BI.createWidget({
            type: "bi.center_adapt",
            cls: "select-data-field-calc-font",
            items: [{
                type: "bi.icon"
            }]
        });
        this.icons.push(icon);
        var items = [{
            type: "bi.htape",
            height: 25,
            items: [{
                el: {
                    type: "bi.layout"
                },
                width: o.layer * 13
            }, {
                el: icon,
                width: 25
            }, {
                type: "bi.label",
                cls: "list-item-text",
                text: o.text,
                value: o.value.name,
                lgap: 10,
                rgap: 5,
                height: 25,
                textAlign: "left"
            }]
        }];
        BI.each(targetIds, function (idx, targetId) {
            var dim = BI.find(o.dimensions, function (idx, dimension) {
                return dimension.dId === targetId;
            });
            self._createSubItems(dim, items);
        });
        items[items.length - 1].items[1].el.cls = "tree-vertical-line-type4";
        this.buttons = BI.createWidget({
            type: "bi.vertical",
            cls: "select-data-level0-item-button",
            element: this.element,
            items: items
        });

        this.buttons.element.draggable(o.drag);
    },

    _createSubItems: function (dim, items) {
        var it = {};
        if (dim.type === BICst.TARGET_TYPE.COUNTER || dim.type === BICst.TARGET_TYPE.NUMBER) {
            it = this._createTemplateItem(dim.name, this.constants.NUMBER);
        } else {
            it = this._createTemplateItem(dim.name, this.constants.CALC);
        }
        var result = BI.find(items, function (idx, item) {
            return BI.isEqual(item, it);
        });
        if (BI.isNull(result)) {
            items.push(it);
        }
    },

    _createTemplateItem: function (text, type) {
        var o = this.options;
        var icon = BI.createWidget({
            type: "bi.center_adapt",
            cls: type === this.constants.NUMBER ? "select-data-field-number-font" : "select-data-field-calc-font",
            items: [{
                type: "bi.icon"
            }]
        });
        this.icons.push(icon);
        return {
            type: "bi.htape",
            height: 25,
            items: [{
                el: {
                    type: "bi.layout"
                },
                width: 13 * o.layer
            }, {
                el: {
                    type: "bi.center_adapt",
                    cls: "tree-vertical-line-type3",
                    items: [{
                        type: "bi.icon",
                        width: 25,
                        height: 25
                    }]
                },
                width: 25
            }, {
                el: icon,
                width: 25
            }, {
                type: "bi.label",
                cls: "list-item-text",
                text: text,
                value: text,
                lgap: 10,
                rgap: 5,
                height: 25,
                textAlign: "left"
            }]
        };
    },

    doClick: function () {
        BI.DetailSelectCalculationTargetLevel0Item.superclass.doClick.apply(this, arguments);
        if (this.isValid()) {
            this.fireEvent(BI.DetailSelectCalculationTargetLevel0Item.EVENT_CHANGE, this.getValue(), this);
        }
    },


    setSelected: function (b) {
        BI.DetailSelectCalculationTargetLevel0Item.superclass.setSelected.apply(this, arguments);
        BI.each(this.icons, function (idx, icon) {
            if (!!b) {
                icon.element.addClass("active");
            } else {
                icon.element.removeClass("active");
            }
        });
        if (!b) {
            this.element.removeClass("select-data-item-top");
            this.element.removeClass("select-data-item-bottom");
        }
    },

    setTopLineVisible: function () {
        this.element.addClass("select-data-item-top");
    },

    setTopLineInVisible: function () {
        this.element.removeClass("select-data-item-top");
    },

    setBottomLineVisible: function () {
        this.element.addClass("select-data-item-bottom");
    },

    setBottomLineInVisible: function () {
        this.element.removeClass("select-data-item-bottom");
    },

    doRedMark: function () {

    },

    unRedMark: function () {

    },

    doHighLight: function () {

    },

    unHighLight: function () {

    }
});

BI.DetailSelectCalculationTargetLevel0Item.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_select_calculation_target_level0_item", BI.DetailSelectCalculationTargetLevel0Item);