FS.THEME.config4MenuTree.insertNodes = [
    function () {
        if (FS.config.isAdmin) {
            return {
                text: BI.i18nText('BI-All_Reports'),
                title: BI.i18nText('BI-All_Reports'),
                src: FR.servletURL + "?op=fr_bi&cmd=bi_init_all_report"
            }
        }
    },
    {
        text: BI.i18nText('FS-Generic-I_Created'),
        title: BI.i18nText('FS-Generic-I_Created'),
        src: FR.servletURL + "?op=fr_bi&cmd=bi_init_created_by_me"
    },
    function () {
        if (!FS.config.isAdmin) {
            return {
                text: BI.i18nText('FS-Generic-Shared_To_Me'),
                title: BI.i18nText('FS-Generic-Shared_To_Me'),
                src: FR.servletURL + "?op=fr_bi&cmd=bi_init_shared_to_me"
            }
        }
    }
];