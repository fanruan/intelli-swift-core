BI.Plugin.registerWidget("bi.detail_select_data", function (w) {
    if (BI.Utils.isRealTime()) {
        w.type = "bi.detail_select_data_4_realtime";
        return w;
    }
});

BI.Plugin.registerWidget("bi.detail_detail_table_select_data", function (w) {
    if (BI.Utils.isRealTime()) {
        w.type = "bi.detail_select_data_4_realtime";
        return w;
    }
});


BI.Plugin.registerWidget("bi.select_string", function (w) {
    if (BI.Utils.isRealTime()) {
        w.type = "bi.select_string_4_realtime";
        return w;
    }
});

BI.Plugin.registerWidget("bi.select_number", function (w) {
    if (BI.Utils.isRealTime()) {
        w.type = "bi.select_number_4_realtime";
        return w;
    }
});

BI.Plugin.registerWidget("bi.select_date", function (w) {
    if (BI.Utils.isRealTime()) {
        w.type = "bi.select_date_4_realtime";
        return w;
    }
});


BI.Plugin.registerWidget("bi.tree_select_data", function (w) {
    if (BI.Utils.isRealTime()) {
        w.type = "bi.select_string_4_realtime";
        return w;
    }
});

BI.Plugin.registerWidget("bi.drag_icon_group", function (w) {
    if (BI.Utils.isRealTime()) {
        w.type = "bi.drag_icon_group_4_real_time";
        return w;
    }
});