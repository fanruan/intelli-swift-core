/**
 * Created by Young's on 2016/6/1.
 */
BI.ReportHangoutPathChooser = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend(BI.ReportHangoutPathChooser.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-report-hangout-path-chooser"
        });
    },

    _init: function(){
        BI.ReportHangoutPathChooser.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function(north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Hangout_Now"),
            height: 50
        })
    },

    rebuildCenter: function(center) {
        
    }
});
$.shortcut("bi.report_hangout_path_chooser", BI.ReportHangoutPathChooser);