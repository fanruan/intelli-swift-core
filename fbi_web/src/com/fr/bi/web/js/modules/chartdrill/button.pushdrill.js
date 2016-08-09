/**
 * Created by Young's on 2016/5/27.
 */
BI.DrillPushButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function () {
        var conf = BI.DrillPushButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-drill-push-button"
        })
    },

    _init: function () {
        BI.DrillPushButton.superclass._init.apply(this, arguments);
        var svg = BI.createWidget({
            type: "bi.svg",
            element: this.element,
            width: 60,
            height: 10
        });
        svg.path("M0,0L60,0 L50,10 L10,10 Z")
            .attr({stroke: "#d4dadd", fill: "#ffffff"});
        this.pushButton = BI.createWidget({
            type: "bi.icon_change_button",
            element: this.element,
            iconWidth: 10,
            iconHeight: 10
        });
    },
    
    setPushDown: function(){
        this.pushButton.setIcon("drill-push-down-icon");
    },
    
    setPushUp: function() {
        this.pushButton.setIcon("drill-push-up-icon");
    },

    doClick: function () {
        BI.DrillPushButton.superclass.doClick.apply(this, arguments);
        if (this.isValid()) {
            this.fireEvent(BI.DrillPushButton.EVENT_CHANGE);
        }
    }
});
BI.DrillPushButton.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.drill_push_button", BI.DrillPushButton);