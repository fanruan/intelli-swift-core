/**
 * @class BI.DimensionTreeTrigger
 * @extends BI.Trigger
 */

BI.DimensionTreeTrigger = BI.inherit(BI.Trigger, {

    _const: {
        hgap: 4,
        font_size: 14,
        triggerWidth: 65
    },

    _defaultConfig: function () {
        return BI.extend(BI.DimensionTreeTrigger.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-tree-trigger",
            height: 30,
            items: []
        });
    },

    _init: function () {
        BI.DimensionTreeTrigger.superclass._init.apply(this, arguments);

        var self = this, o = this.options, c = this._const;

        this.fieldtext = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            cls: "field-label",
            height: o.height,
            hgap: c.hgap
        });

        this.tableText = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            cls: "table-label",
            height: o.height,
            hgap: c.hgap
        });

        this.trigerButton = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "dimension-change-button pull-down-font",
            height: o.height,
            textLgap: 5,
            direction: BI.Direction.Right,
            text: BI.i18nText("BI-Change")
        });

        this.wrap = BI.createWidget({
            type: "bi.htape",
            items: [{
                el: this.fieldtext,
                width: BI.DOM.getTextSizeWidth(this.fieldtext.getValue(), this._const.font_size) + 2 * this._const.hgap
            }, this.tableText]
        });

        BI.createWidget({
            element: this.element,
            type: 'bi.htape',
            items: [this.wrap, {
                el: this.trigerButton,
                width: c.triggerWidth
            }]
        });
    },

    _resizeLayout: function () {
        this.wrap.attr("items")[0].width = BI.DOM.getTextSizeWidth(this.fieldtext.getValue() ,this._const.font_size) + 2 * this._const.hgap;
        this.wrap.resize();
    },

    setValue: function (v) {
        var self = this;
        v = BI.isArray(v) ? v : [v];
        this.options.value = v;
        BI.any(this.options.items, function (i, item) {
            if (v.contains(item.value)) {
                self.fieldtext.setValue(item.text || item.value);
                self.tableText.setValue("(" + BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(item.value)) + ")");
                var title = (item.text || item.value) + "(" + BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(item.value)) + ")";
                self.fieldtext.setTitle(title);
                self.tableText.setTitle(title);
                self._resizeLayout();
                return true;
            }
        });
    },

    getValue: function () {
        return this.options.value || [];
    },

    populate: function (items) {
        BI.DimensionTreeTrigger.superclass.populate.apply(this, arguments);
        this.options.items = items;
    }

});

$.shortcut("bi.dimension_tree_trigger", BI.DimensionTreeTrigger);