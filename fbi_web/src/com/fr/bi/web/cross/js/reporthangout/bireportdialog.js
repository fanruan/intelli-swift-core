/**
 * Created by Young's on 2016/6/2.
 */
BI.BIReportDialog = BI.inherit(FR.OB, {
    _init: function(){
        var self = this, o = this.options;
        var id = BI.UUID();
        var popover = BI.createWidget({
            type: "bi.plate_hangout_report",
            report: o.report
        });
        popover.on(BI.PlateHangoutReport.EVENT_SAVE, function () {
            var data = popover.getValue();
            o.onSave(data);
            BI.Popovers.remove(id);
        });
        BI.Popovers.create(id, popover, {width: 400, height: 340}).open(id);
    }
});