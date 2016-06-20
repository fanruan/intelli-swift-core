/**
 * @class BI.HyperLinkInsert
 * @extends BI.Widget
 */
BI.HyperLinkInsert = BI.inherit(BI.Widget, {


    _defaultConfig: function () {
        var conf = BI.HyperLinkInsert.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-hyper-link-insert",
            dId: ""
        })
    },

    _init: function () {
        BI.HyperLinkInsert.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.selectField = BI.createWidget({
            type: "bi.button_group",
            cls: "select-field",
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.selectField.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            var v = self.selectField.getValue()[0];
            self.textArea.insertParam(v);
        });

        this.used = BI.createWidget({
            type: "bi.multi_select_item",
            text: BI.i18nText("BI-Use_HyperLink")
        });

        this.used.on(BI.Controller.EVENT_CHANGE, function(){
            var b = self.used.isSelected();
            self.selectField.setEnable(!!b);
            self.textArea.setEnable(!!b);
        });


        this.textArea = BI.createWidget({
            type: "bi.code_editor",
            cls: "hyper-link-text-area"
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.center_adapt",
                    items: [this.used]
                },
                height: 40
            }, {
                el: {
                    type: "bi.htape",
                    hgap: 5,
                    items: [
                        {
                            width: 200,
                            el: self.selectField
                        },
                        {
                            el: self.textArea
                        }
                    ]
                }
            }]
        });
    },

    _assertValue: function (v) {
        v = v || {};
        v.expression = v.expression || "";
        v.used = v.used || true;
        return v;
    },

    setValue: function (v) {
        v = this._assertValue(v);
        this.used.setSelected(!!v.used) ;
        this.textArea.setValue(v.expression);
    },

    getValue: function () {
        var value = this.textArea.getValue();
        return {
            expression: value,
            used: this.used.isSelected()
        };
    },

    populate: function () {
        var o = this.options;
        var dimensionName = BI.Utils.getDimensionNameByID(o.dId);
        this.selectField.populate([{
            type: "bi.text_button",
            hgap: 5,
            textAlign: "left",
            height: 25,
            text: dimensionName,
            cls: "select-field-item",
            value: dimensionName,
            once: false
        }]);
        this.setValue(BI.Utils.getDimensionHyperLinkByID(o.dId));
    }


});
BI.HyperLinkInsert.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.hyper_link_insert", BI.HyperLinkInsert);