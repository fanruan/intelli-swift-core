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
                case BICst.STATISTICS_GROUP_DATE_COMBO.DELETE:
                    self.fireEvent(BI.AbstractDimension.EVENT_DESTROY);
                    break;
            }
        });
        return combo;
    }
});

$.shortcut("bi.date_group_dimension", BI.DateGroupDimension);