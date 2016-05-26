/**
 * @class BI.NumberStatisticDimension
 * @extends BI.Widget
 * @author windy
 */
BI.NumberStatisticDimension = BI.inherit(BI.AbstractDimension, {

    constants: {
        DIMENSION_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 25,
        LABEL_GAP : 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.NumberStatisticDimension.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function () {
        BI.NumberStatisticDimension.superclass._init.apply(this, arguments);
    },

    _createCombo: function () {
        var self = this, o = this.options;
        o.groupOrSummary = 1;
        o.fieldType = BICst.COLUMN.NUMBER;
        var combo = BI.createWidget({
            type: "bi.statistic_number_combo",
            dimension: o.model.getDimension(o.dId)
        });
        combo.on(BI.StatisticNumberCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.SUM:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.SUMMARY_TYPE.SUM});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.AVG:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.SUMMARY_TYPE.AVG});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.MAX:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.SUMMARY_TYPE.MAX});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.MIN:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.SUMMARY_TYPE.MIN});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.No_Repeat_Count:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.SUMMARY_TYPE.COUNT});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.RECORD_COUNT:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.SUMMARY_TYPE.RECORD_COUNT});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.DISPLAY:
                    o.model.setDimensionUsedById(o.dId, true);
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.HIDDEN:
                    o.model.setDimensionUsedById(o.dId, false);
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.RENAME:
                    self.nameEditor.focus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.DELETE:
                    self.fireEvent(BI.AbstractDimension.EVENT_DESTROY);
                    break;
            }
        });
        return combo;
    }
});
$.shortcut("bi.number_statistic_dimension", BI.NumberStatisticDimension);