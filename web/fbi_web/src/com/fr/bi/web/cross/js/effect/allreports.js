/**
 * Created by Young's on 2016/5/30.
 */
(function ($) {

    $.extend(FS, {

        allReports: function ($tab, $content, entry) {
            entry.contentEl.empty();
            var allReports = BI.createWidget({
                type: "bi.all_reports",
                element: entry.contentEl
            });
            allReports.populate();
            allReports.on(BI.AllReports.EVENT_HANGOUT, function (report, data) {
                var bi = {
                    reportId: report.id,
                    reportName: report.text,
                    createBy: report.createBy,
                    parentId: data.parentId,
                    description: data.description,
                    text: data.text
                };
                BI.requestAsync("fr_bi", "hangout_report_to_plate", {report: bi}, function (res) {
                    BI.Msg.toast(BI.i18nText("BI-Report_Hangout_Success"));
                });
            });
        }
    });
})(jQuery);