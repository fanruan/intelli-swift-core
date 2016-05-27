/**
 * Created by Young's on 2016/5/27.
 */
BI.DrillPushButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function(){
        return BI.extend(BI.DrillPushButton.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-drill-push-button"
        })
    },

    _init: function(){
        BI.DrillPushButton.superclass._init.apply(this, arguments);
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: {
                    type: "bi."
                }
            }]
        });
    }
});
$.shortcut("bi.drill_push_button", BI.DrillPushButton);