/**
 * Created by roy on 15/12/4.
 */
BI.FormulaFieldButton = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.FormulaFieldButton.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-formula-field-button",
            height: 20,
            formulaString: "",
            formulaValue: "",
            newFieldName: ""
        })
    },
    _init: function () {
        var self = this, o = this.options;
        BI.FormulaFieldButton.superclass._init.apply(this, arguments);

        var deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "search-close-h-font"
        });

        var configButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "detail-dimension-set-font"
        });


        deleteButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.FormulaFieldButton.EVENT_CLICK_DELETE, self)
        });

        configButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.FormulaFieldButton.EVENT_CLICK_SET, self.attr("id"))
        });


        this.formulaLabel = BI.createWidget({
            type: "bi.label",
            cls: "bi-etl-formula-list-button-formula-label right-label",
            value: o.formulaString
        });

        this.fieldLabel = BI.createWidget({
            type: "bi.label",
            cls: "left-label",
            value: o.fieldName
        });

        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            element: this.element,
            height: o.height,
            width: o.width,
            items: {
                left: [
                    {
                        el: this.fieldLabel
                    },
                    {
                        el: this.formulaLabel
                    }
                ],
                right: [
                    {
                        el: configButton
                    },
                    {
                        el: deleteButton
                    }
                ]

            },
            lhgap: 5,
            rhgap: 5
        });

        this.element.hover(function () {
            deleteButton.setVisible(true);
            configButton.setVisible(true);
        }, function () {
            deleteButton.setVisible(false);
            configButton.setVisible(false);
        });

        deleteButton.setVisible(false);
        configButton.setVisible(false);
    },


    getValue: function () {
        var value = {};
        value.formulaString = this.formulaLabel.getValue();
        value.fieldName = this.fieldLabel.getValue();
        value.formulaValue = this.options.formulaValue;
        value.id = this.options.id;
        return value;
    }

});
BI.FormulaFieldButton.EVENT_CLICK_DELETE = "EVENT_CLICK_DELETE";
BI.FormulaFieldButton.EVENT_CLICK_SET = "EVENT_CLICK_SET";
$.shortcut("bi_formula_field_button", BI.FormulaFieldButton);