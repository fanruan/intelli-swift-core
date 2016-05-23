/**
 * @class BI.DetailSelectCalculationTargetLevel0Item
 * @extends BI.Single
 */
BI.DetailSelectCalculationTargetLevel0Item = BI.inherit(BI.Single, {

    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectCalculationTargetLevel0Item.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-calc-data-level0-item bi-select-data-level0-item",
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
        this.buttons = BI.createWidget({
            type: "bi.detail_select_calculation_target_level0_button",
            trigger: "mousedown",
            cls: "select-data-level0-item-button",
            expression: expression,
            dimensions: o.dimensions,
            layer: o.layer,
            value: o.value
        });

        this.buttons.on(BI.Controller.EVENT_CHANGE, function (type) {
            if (type === BI.Events.CLICK) {
                self.setSelected(self.isSelected());
            }
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.CLICK, self.getValue(), self);
        });
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.buttons]
        });

        this.buttons.element.draggable(o.drag);

        BI.Broadcasts.on(BICst.BROADCAST.SRC_PREFIX + o.id, function (v) {
            self.setSelected(false);
        });
    },

    isSelected: function () {
        return this.buttons.isSelected();
    },

    setSelected: function (b) {
        this.buttons.setSelected(b);
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
        this.buttons.doRedMark.apply(this.buttons, arguments);
    },

    unRedMark: function () {
        this.buttons.unRedMark.apply(this.buttons, arguments);
    },

    doHighLight: function () {
        this.buttons.doHighLight.apply(this.buttons, arguments);
    },

    unHighLight: function () {
        this.buttons.unHighLight.apply(this.buttons, arguments);
    }
});

BI.DetailSelectCalculationTargetLevel0Item.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_select_calculation_target_level0_item", BI.DetailSelectCalculationTargetLevel0Item);