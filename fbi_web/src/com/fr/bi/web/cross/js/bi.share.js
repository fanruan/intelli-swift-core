FS.THEME.config4MenuTree.insertNodes = [
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