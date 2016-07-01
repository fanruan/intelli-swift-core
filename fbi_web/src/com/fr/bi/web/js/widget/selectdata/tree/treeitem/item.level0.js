/**
 * Created by GUY on 2015/9/6.
 * @class BI.SelectDataLevel0Item
 * @extends BI.Single
 */
BI.SelectDataLevel0Item = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectDataLevel0Item.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-data-level0-item",
            height: 25,
            hgap: 0,
            fieldType: BICst.COLUMN.STRING,
            lgap: 0,
            rgap: 0
        })
    },

    _getFieldClass: function (type) {
        switch (type) {
            case BICst.COLUMN.STRING:
                return "select-data-field-string-font";
            case BICst.COLUMN.NUMBER:
                return "select-data-field-number-font";
            case BICst.COLUMN.DATE:
                return "select-data-field-date-font";
            case BICst.COLUMN.COUNTER:
                return "select-data-field-number-font";
        }
    },

    _init: function () {
        BI.SelectDataLevel0Item.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.button = BI.createWidget({
            type: "bi.blank_icon_text_item",
            trigger: "mousedown",
            cls: "select-data-level0-item-button " + this._getFieldClass(o.fieldType),
            blankWidth: 20,
            text: o.text,
            value: o.value,
            height: 25,
            textLgap: 10,
            textRgap: 5
        });
        this.button.on(BI.Controller.EVENT_CHANGE, function (type) {
            if (type === BI.Events.CLICK) {
                self.setSelected(self.isSelected());
            }
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.CLICK, self.getValue(), self);
        });

        this.topLine = BI.createWidget({
            type: "bi.layout",
            height: 0,
            cls: "select-data-top-line"
        });
        this.bottomLine = BI.createWidget({
            type: "bi.layout",
            height: 0,
            cls: "select-data-bottom-line"
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.topLine,
                top: 0,
                left: o.lgap,
                right: o.rgap
            }, {
                el: this.bottomLine,
                bottom: 0,
                left: o.lgap,
                right: o.rgap
            }, {
                el: this.button,
                top: 0,
                left: o.lgap,
                right: o.rgap
            }]
        });
        this.topLine.invisible();
        this.bottomLine.invisible();
    },

    isSelected: function () {
        return this.button.isSelected();
    },

    setSelected: function (b) {
        this.button.setSelected(b);
        if (!b) {
            this.topLine.invisible();
            this.bottomLine.invisible();
            this.element.removeClass("select-data-item-top");
            this.element.removeClass("select-data-item-bottom");
        }
    },

    setTopLineVisible: function () {
        this.topLine.visible();
        this.element.addClass("select-data-item-top");
    },

    setTopLineInVisible: function () {
        this.topLine.invisible();
        this.element.removeClass("select-data-item-top");
    },

    setBottomLineVisible: function () {
        this.bottomLine.visible();
        this.element.addClass("select-data-item-bottom");
    },

    setBottomLineInVisible: function () {
        this.bottomLine.invisible();
        this.element.removeClass("select-data-item-bottom");
    },

    doRedMark: function () {
        this.button.doRedMark.apply(this.button, arguments);
    },

    unRedMark: function () {
        this.button.unRedMark.apply(this.button, arguments);
    },

    doHighLight: function () {
        this.button.doHighLight.apply(this.button, arguments);
    },

    unHighLight: function () {
        this.button.unHighLight.apply(this.button, arguments);
    }
});

$.shortcut("bi.select_data_level0_item", BI.SelectDataLevel0Item);