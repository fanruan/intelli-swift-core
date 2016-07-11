//工程配置
$(function () {
    BI.isSupportFlex = BI.isSupportCss3("flex");
    //BI.Plugin.registerWidget("bi.center_adapt", function (ob) {
    //    if (BI.isSupportFlex) {
    //        return BI.extend({}, ob, {type: "bi.flexbox_center_adapt"});
    //    } else {
    //        return ob;
    //    }
    //});
    BI.Plugin.registerWidget("bi.float_center_adapt", function (ob) {
        if (BI.isSupportFlex) {
            return BI.extend({}, ob, {type: "bi.flexbox_center_adapt"});
        } else {
            return ob;
        }
    });

    // BICst.CONFIG.SHOW_DASHBOARD_TITLE = true;
});
