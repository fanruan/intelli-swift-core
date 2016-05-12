/**
 * Created by GUY on 2015/7/3.
 */
BIDezi.TargetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.TargetModel.superclass._defaultConfig.apply(this, arguments), {
            _src: {},
            group: {type: BICst.SUMMARY_TYPE.SUM},
            used: true
        });
    },

    _init: function () {
        BIDezi.TargetModel.superclass._init.apply(this, arguments);
    },

    refresh: function () {

    },

    local: function () {
        return false;
    },

    change: function () {

    },

    destroy: function () {
        var dIds = BI.Utils.getDimensionUsedByOtherDimensionsByDimensionID(this.get("id"));
        if (dIds.length > 0) {
            BI.Msg.alert(BI.i18nText("BI-Failure_Toast"), BI.i18nText("BI-Target_Used_In_Calculate_Cannot_Delete", dIds.toString()));
        } else {
            BIDezi.TargetModel.superclass.destroy.apply(this, arguments);
        }
    }
});