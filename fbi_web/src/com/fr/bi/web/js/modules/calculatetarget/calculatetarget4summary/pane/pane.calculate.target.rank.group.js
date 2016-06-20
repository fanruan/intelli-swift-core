BI.CalculateTargetRankGroupPane = BI.inherit(BI.CalculateTargetAbstractGroupPane, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetRankGroupPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-rank-group-pane"
        })
    },

    _init: function () {
        BI.CalculateTargetRankGroupPane.superclass._init.apply(this, arguments);
        this.principleCombo.populate(BICst.CAL_TARGET_RANK_TYPE);
        this.principleCombo.setValue(BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC);
    },

    _refreshLabel: function () {
        var self = this, o = this.options;
        this.logicPane.empty();
        BI.each(o.model.getDimDimensionIDs(), function (i, dId) {
            var dimensionName = BI.Utils.getDimensionNameByID(dId);
            var label = BI.createWidget({
                type: "bi.label",
                textHeight: 30,
                textAlign: "left"
            });
            label.setValue(BI.i18nText("BI-Calculate_Target_Include_In_Same", dimensionName));
            self.logicPane.addItem(label);
        });
        var lastLabel = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            textHeight: 30
        });
        switch (this.principleCombo.getValue()[0]) {
            case BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC:
                lastLabel.setValue(BI.i18nText("BI-Calculate_Target_Rank_Group_ASC", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
            case BICst.TARGET_TYPE.CAL_VALUE.RANK_TPYE.DESC:
                lastLabel.setValue(BI.i18nText("BI-Calculate_Target_Rank_Group_DESC", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
        }
        this.logicPane.addItem(lastLabel);
        this.tipLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Group_Tip_Label", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])))
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
$.shortcut("bi.calculate_target_rank_group_pane", BI.CalculateTargetRankGroupPane);