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
        }
    });
})(jQuery);