//工程配置
$(function () {
    BI.isSupportFlex = BI.isSupportCss3("flex");
    BI.Plugin.registerWidget("bi.horizontal", function (ob) {
        if (BI.isSupportFlex) {
            return BI.extend({}, ob, {type: "bi.flex_horizontal"});
        } else {
            return ob;
        }
    });
    BI.Plugin.registerWidget("bi.center_adapt", function (ob) {
       if (BI.isSupportFlex) {
           return BI.extend({}, ob, {type: "bi.flex_center"});
       } else {
           return ob;
       }
    });
    BI.Plugin.registerWidget("bi.vertical_adapt", function (ob) {
        if (BI.isSupportFlex) {
            return BI.extend({}, ob, {type: "bi.flex_vertical_center"});
        } else {
            return ob;
        }
    });
    BI.Plugin.registerWidget("bi.float_center_adapt", function (ob) {
        if (BI.isSupportFlex) {
            return BI.extend({}, ob, {type: "bi.flex_center"});
        } else {
            return ob;
        }
    });
});