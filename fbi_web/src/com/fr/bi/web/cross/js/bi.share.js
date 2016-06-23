FS.THEME.config4MenuTree.insertNodes = [
    function () {
        if (FS.config.isAdmin) {
            return {
                text: BI.i18nText("BI-All_Reports"),
                contentEl: $('<div>'),
                afterLoad: function ($tab, $content, entry) {
                    FS.allReports.apply(this, [$tab, $content, entry]);
                }
            }
        }
    },
    {
        text: BI.i18nText('FS-Generic-I_Created'),
        contentEl: $('<div>'),
        afterLoad: function ($tab, $content, entry) {
            FS.createByMe.apply(this, [$tab, $content, entry]);
        }
    },
    function () {
        if (!FS.config.isAdmin) {
            return {
                text: BI.i18nText('FS-Generic-Shared_To_Me'),
                contentEl: $('<div>'),
                afterLoad: function ($tab, $content, entry) {
                    FS.shareToMe.apply(this, [$tab, $content, entry]);
                }
            }
        }
    }
];