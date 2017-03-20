/**
 * @class BI.DateGroupDimension
 * @extends BI.Widget
 * @author windy
 */
BI.DateGroupDimension = BI.inherit(BI.AbstractDimension, {

    constants: {
        DIMENSION_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 25,
        LABEL_GAP : 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.DateGroupDimension.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function () {
        BI.DateGroupDimension.superclass._init.apply(this, arguments);
    },

    _createCombo: function () {
        var self = this, o = this.options;
        o.groupOrSummary = 0;
        o.fieldType = BICst.COLUMN.DATE;
        var combo = BI.createWidget({
            type: "bi.group_date_combo",
            dimension: o.model.getDimension(o.dId)
        });
        combo.on(BI.GroupDateCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.STATISTICS_GROUP_DATE_COMBO.DATE:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.YMD});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.YEAR:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.Y});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.QUARTER:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.S});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.MONTH:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.M});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.WEEK:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.W});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.YS:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.YS});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.YMDHM:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.YMDHM});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.YMDH:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.YMDH});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.YW:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.YW});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.YM:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.YM});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.D:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.D});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.WEEK_COUNT:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.WEEK_COUNT});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.SECOND:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.SECOND});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.MINUTE:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.MINUTE});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.HOUR:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.HOUR});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.MD:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.MD});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.YMDHMS:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.YMDHMS});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_DATE_COMBO.YD:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.YD});
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

$.shortcut("bi.date_group_dimension", BI.DateGroupDimension);