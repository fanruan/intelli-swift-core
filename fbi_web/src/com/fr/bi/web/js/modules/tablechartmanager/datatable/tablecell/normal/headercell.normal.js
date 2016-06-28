/**
 * created by young
 * 默认风格表格——表头
 */
BI.NormalHeaderCell = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.NormalHeaderCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-normal-header-cell",
            width: "100%",
            height: 25
        })
    },

    _init: function () {
        BI.NormalHeaderCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var dId = o.dId;
        var combo = BI.createWidget();
        var name = o.text;
        if (BI.Utils.isDimensionByDimensionID(dId)) {
            combo = BI.createWidget({
                type: "bi.sort_filter_dimension_combo",
                dId: dId
            });
            combo.on(BI.SortFilterDimensionCombo.EVENT_CHANGE, function (v) {
                o.sortFilterChange(v);
            });
        } else {
            //是指标，
            combo = BI.createWidget({
                type: "bi.sort_filter_target_combo",
                dId: dId
            });
            combo.on(BI.SortFilterTargetCombo.EVENT_CHANGE, function (v) {
                o.sortFilterChange(v);
            });
        }
        var styleSettings = BI.Utils.getDimensionSettingsByID(dId);
        var st = this._getNumLevelByLevel(styleSettings.num_level) + (styleSettings.unit || "");
        if (BI.isNotEmptyString(st)) {
            name = name + "(" + st + ")";
        }
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.label",
                    text: name,
                    title: name,
                    cls: "header-cell-text",
                    whiteSpace: "nowrap",
                    textAlign: "left",
                    lgap: 5,
                    height: 25
                }
            }, {
                el: {
                    type: "bi.center_adapt",
                    items: [combo],
                    width: 25,
                    height: 25
                },
                width: 25
            }]
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
BI.NormalHeaderCell.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.normal_header_cell", BI.NormalHeaderCell);