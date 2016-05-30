FS.THEME.config4MenuTree.insertNodes = [
    {
        text: BI.i18nText("BI-All_Reports"),
        contentEl: $('<div>'),
        afterLoad: function ($tab, $content, entry) {
            FS.allReports.apply(this, [$tab, $content, entry]);
        }
    },
    {
        text: BI.i18nText('FS-Generic-I_Created'),
        contentEl: $('<div>'),
        afterLoad: function ($tab, $content, entry) {
            FS.createByMe.apply(this, [$tab, $content, entry]);
        }
    },
    {
        text: BI.i18nText('FS-Generic-Shared_To_Me'),
        contentEl: $('<div>'),
        afterLoad: function ($tab, $content, entry) {
            FS.shareToMe.apply(this, [$tab, $content, entry]);
        }
    }
];
// (function ($) {
//     FS.THEME.config4MenuTree.insertNodes = [];
//    
//     FS.THEME.config4MenuTree.insertNodes.push({
//         text: BI.i18nText('FS-Generic-I_Created'),
//         contentEl: $('<div>'),
//         afterLoad: function ($tab, $content, entry) {
//             FS.createByMe.apply(this, [$tab, $content, entry]);
//         }
//     });
//     FS.THEME.config4MenuTree.insertNodes.push({
//         text: BI.i18nText('FS-Generic-Shared_To_Me'),
//         contentEl: $('<div>'),
//         afterLoad: function ($tab, $content, entry) {
//             FS.shareToMe.apply(this, [$tab, $content, entry]);
//         }
//     });
// })(jQuery);