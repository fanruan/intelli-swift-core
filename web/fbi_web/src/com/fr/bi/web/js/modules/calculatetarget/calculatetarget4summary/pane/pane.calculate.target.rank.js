BI.CalculateTargetRankPane = BI.inherit(BI.CalculateTargetAbstractPane, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetRankPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-rank-pane"
        })
    },

    _init: function () {
        BI.CalculateTargetRankPane.superclass._init.apply(this, arguments);
        this.principleCombo.populate(BICst.CAL_TARGET_RANK_TYPE);
        this.principleCombo.setValue(BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC);
    },

    _refreshLabel: function () {
        switch (this.principleCombo.getValue()[0]) {
            case BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC:
                this.logicLabel.setValue(BI.i18nText("BI-Calculate_Target_Rank_ASC", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
            case BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.DESC:
                this.logicLabel.setValue(BI.i18nText("BI-Calculate_Target_Rank_DESC", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
        }

    },

    setValue: function (expression) {
        this.principleCombo.setValue(expression.rank_type);
        this.valueCombo.setValue(expression.ids);
        this._refreshLabel();
    },

    getValue: function () {
        var result = {};
        result.rank_type = this.principleCombo.getValue()[0];
        result.ids = this.valueCombo.getValue();
        return result;
    }
});
$.shortcut("bi.calculate_target_rank_pane", BI.CalculateTargetRankPane);