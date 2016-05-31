/**
 * Created by Young's on 2016/4/15.
 */
BI.DetailTableHeader = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTableHeader.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-header"
        })
    },

    _init: function () {
        BI.DetailTableHeader.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var dId = o.dId;
        var name = o.text;
        var combo = BI.createWidget({
            type: "bi.sort_filter_target_combo",
            dId: dId
        });
        combo.on(BI.SortFilterTargetCombo.EVENT_CHANGE, function (v) {
            o.sortFilterChange(v);
        });
        var styleSettings = BI.Utils.getDimensionSettingsByID(dId);
        var st = this._getNumLevelByLevel(styleSettings.num_level) + (styleSettings.unit || "");
        if (BI.isNotEmptyString(st)) {
            name = name + "(" + st + ")";
        }
        BI.createWidget({
            type: "bi.horizontal_adapt",
            element: this.element,
            items: [{
                type: "bi.label",
                text: name,
                title: name,
                cls: "header-cell-text",
                whiteSpace: "nowrap",
                textAlign: "left",
                lgap: 5,
                height: 36
            }, {
                type: "bi.default",
                items: [combo],
                width: 25,
                height: 25
            }],
            columnSize: ["", 25]
        });
    },

    _getNumLevelByLevel: function (level) {
        var numLevel = "";
        BI.each(BICst.TARGET_STYLE_LEVEL, function (i, ob) {
            if (ob.value === level && level !== BICst.TARGET_STYLE.NUM_LEVEL.NORMAL) {
                numLevel = ob.text;
            }
        });
        return numLevel;
    }
});
$.shortcut("bi.detail_table_header", BI.DetailTableHeader);