/**
 * @class BI.DateStatisticDimension
 * @extends BI.Widget
 *
 */
BI.DateStatisticDimension = BI.inherit(BI.AbstractDimension, {

    constants: {
        DIMENSION_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 25,
        LABEL_GAP : 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.DateStatisticDimension.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function () {
        BI.DateStatisticDimension.superclass._init.apply(this, arguments);
    },

    _createCombo: function () {
        var self = this, o = this.options;
        o.groupOrSummary = 1;
        o.fieldType = BICst.COLUMN.DATE;
        var combo = BI.createWidget({
            type: "bi.statistic_date_combo",
            dimension: o.model.getDimension(o.dId)
        });
        combo.on(BI.StatisticDateCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.STATISTICS_GROUP_DATE_COMBO.No_Repeat_Count:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.SUMMARY_TYPE.COUNT});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.RECORD_COUNT:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.SUMMARY_TYPE.RECORD_COUNT});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.RENAME:
                    self.nameEditor.focus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.DELETE:
                    self.fireEvent(BI.AbstractDimension.EVENT_DESTROY);
                    break;
            }
        });
        return combo;
    }
});
$.shortcut("bi.date_statistic_dimension", BI.DateStatisticDimension);