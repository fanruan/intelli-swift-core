/**
 * @class BI.JoinTypeButton
 * @extend BI.Widget
 * join type button
 */
BI.JoinTypeButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function(){
        var conf = BI.JoinTypeButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend( conf, {
            baseCls: (conf.baseCls || "")+ " bi-join-type-button",
            height: 115,
            width: 96
        });
    },

    _init: function(){
        BI.JoinTypeButton.superclass._init.apply(this, arguments);
        var o = this.options;
        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [{
                type: "bi.vertical",
                cls: o.iconCls,
                items: [{
                    type: "bi.center_adapt",
                    items: [{
                        type: "bi.icon",
                        height: 32,
                        width: 32
                    }]
                }, {
                    type: "bi.label",
                    text: o.text
                }],
                vgap: 5
            }]
        })
    },

    getValue: function(){
        return this.options.value;
    }
});
$.shortcut("bi.join_type_button", BI.JoinTypeButton);