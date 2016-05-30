/**
 * Created by Young's on 2016/5/30.
 */
(function ($) {

    $.extend(FS, {

        allReports: function ($tab, $content, entry) {
            entry.contentEl.empty();
            BI.createWidget({
                type: "bi.all_reports",
                element: entry.contentEl
            })
        }
    });
})(jQuery);