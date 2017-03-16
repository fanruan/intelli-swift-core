/**
 * Created by roy on 15/12/4.
 */
BI.FormulaList = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.FormulaList.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-formula-list"
        })
    },
    _init: function () {
        var o = this.options;
        BI.FormulaList.superclass._init.apply(this, arguments);
        this.formulaList = BI.createWidget({
            type: "bi.vertical",
            width: o.width,
            element: this.element
        })
    },

    _createFieldWidget: function (item) {
        var self = this;
        var fieldWidget = BI.createWidget({
            type: "bi_formula_field_button",
            formulaValue: item.formulaValue,
            formulaString:item.formulaString,
            fieldName: item.fieldName,
            id: item.id
        });

        fieldWidget.on(BI.FormulaFieldButton.EVENT_CLICK_SET, function (id) {
            self.fireEvent(BI.FormulaList.EVENT_CLICK_SET, id)
        });

        fieldWidget.on(BI.FormulaFieldButton.EVENT_CLICK_DELETE, function (obj) {
            obj.destroy();
            self.fireEvent(BI.FormulaList.EVENT_CLICK_DELETE, obj.attr("id"));
        });
        return fieldWidget;
    },

    populate: function (items) {
        var self = this;
        var buttons = [];
        BI.each(items, function (i, item) {
            buttons.push(self._createFieldWidget(item))
        });
        this.formulaList.empty();
        this.formulaList.populate(buttons);
    }
});
BI.FormulaList.EVENT_CLICK_SET = "EVENT_CLICK_SET";
BI.FormulaList.EVENT_CLICK_DELETE = "EVENT_CLICK_DELETE";
$.shortcut("bi.formula_list", BI.FormulaList);