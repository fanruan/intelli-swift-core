/**
 * @class BI.RealDataCheckbox
 * @type {*|void}
 */
BI.RealDataCheckbox = BI.inherit(BI.Widget, {

    constants: {
        HEIGHT: 40,
        WIDTH: 40
    },

    _defaultConfig: function(){
        return BI.extend(BI.RealDataCheckbox.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-widget-real-data"
        })
    },

    _init: function(){
        BI.RealDataCheckbox.superclass._init.apply(this, arguments);
        this.checkbox = BI.createWidget({
            type: "bi.checkbox"
        });
        BI.createWidget({
            type: "bi.left",
            element: this.element,
            height: this.constants.HEIGHT,
            items: [{
                type: "bi.center",
                items: [this.checkbox],
                height: this.constants.HEIGHT,
                width: this.constants.WIDTH
            }, {
                type: "bi.label",
                height: this.constants.HEIGHT,
                text: BI.i18nText("BI-Use_Real_Data")
            }, {
                type: "bi.center_adapt",
                cls: "detail-real-data-warning-font",
                height: this.constants.HEIGHT,
                width: this.constants.WIDTH,
                items: [{
                    type: "bi.icon"
                }]
            }, {
                type: "bi.label",
                cls: "bi-real-data-warning",
                height: this.constants.HEIGHT,
                text: BI.i18nText("BI-Use_Real_Data_Info")
            }]
        })
    }
});
$.shortcut("bi.real_data_checkbox", BI.RealDataCheckbox);