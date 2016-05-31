(function ($) {

    $.extend(FS, {

        shareToMe: function ($tab, $content, entry) {
            entry.contentEl.empty();
            BI.requestAsync("fr_bi", "get_share_2_me_report_list", {}, function (data) {

            });
        }
    });
})(jQuery);