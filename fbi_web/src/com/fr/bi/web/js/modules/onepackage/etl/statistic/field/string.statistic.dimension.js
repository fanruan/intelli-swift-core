/**
 * @class BI.StringStatisticDimension
 * @extends BI.Widget
 * @author windy
 */
BI.StringStatisticDimension = BI.inherit(BI.AbstractDimension, {

    _defaultConfig: function () {
        return BI.extend(BI.StringStatisticDimension.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function () {
        BI.StringStatisticDimension.superclass._init.apply(this, arguments);
    },

    _createCombo: function () {
        var self = this,o = this.options;
        o.groupOrSummary = 1;
        o.fieldType = BICst.COLUMN.STRING;
        var combo = BI.createWidget({
            type: "bi.statistic_string_combo",
            dimension: o.model.getDimension(o.dId)
        });
        combo.on(BI.StatisticStringCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.STATISTICS_GROUP_STRING_COMBO.No_Repeat_Count:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.SUMMARY_TYPE.COUNT});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_STRING_COMBO.APPEND:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.SUMMARY_TYPE.APPEND});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_STRING_COMBO.RECORD_COUNT:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.SUMMARY_TYPE.RECORD_COUNT});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_STRING_COMBO.RENAME:
                    self.nameEditor.focus();
                    break;
                case BICst.STATISTICS_GROUP_STRING_COMBO.DELETE:
                    self.fireEvent(BI.AbstractDimension.EVENT_DESTROY);
                    break;
            }
        });
        return combo;
    }
});
$.shortcut("bi.string_statistic_dimension", BI.StringStatisticDimension);