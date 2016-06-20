(function ($) {

    $.extend(FS, {

        shareToMe: function ($tab, $content, entry) {
            entry.contentEl.empty();
            BI.requestAsync("fr_bi", "get_share_2_me_report_list", {}, function (data) {
                //简单的界面，没有什么操作，直接这边写了
                var shareToMe = BI.createWidget({
                    type: "bi.share_to_me",
                    element: entry.contentEl
                });
                shareToMe.populate(data);
            });
        }
    });
})(jQuery);