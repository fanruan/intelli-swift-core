/**
 * Created by Young's on 2016/5/27.
 */
BI.DrillPushButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function () {
        var conf = BI.DrillPushButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-drill-push-button",
            width: 60,
            height: 10
        })
    },

    _init: function () {
        BI.DrillPushButton.superclass._init.apply(this, arguments);
        var svg = BI.createWidget({
            type: "bi.svg",
            width: 60,
            height: 10
        });
        svg.path("M0,0L60,0 L50,10 L10,10 Z")
            .attr({stroke: "#d4dadd", fill: "#ffffff"});
        this.pushButton = BI.createWidget({
            type: "bi.icon_change_button",
            iconWidth: 10,
            iconHeight: 10
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: svg,
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }, {
                el: this.pushButton,
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }]
        })
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