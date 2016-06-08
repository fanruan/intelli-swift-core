/**
 * Created by GUY on 2015/7/3.
 */
BIShow.TargetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.TargetModel.superclass._defaultConfig.apply(this, arguments), {
            _src: {},
            group: {type: BICst.SUMMARY_TYPE.SUM},
            used: true
        });
    },

    _init: function () {
        BIShow.TargetModel.superclass._init.apply(this, arguments);
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
            var str = "";
            BI.each(dIds, function (i, dId) {
                if (i === 0) {
                    str = BI.Utils.getDimensionNameByID(dId);
                } else {
                    str += "," + BI.Utils.getDimensionNameByID(dId);
                }
            });
            BI.Msg.alert(BI.i18nText("BI-Failure_Toast"), BI.i18nText("BI-Target_Used_In_Calculate_Cannot_Delete", str));
        } else {
            BIShow.TargetModel.superclass.destroy.apply(this, arguments);
        }
    }
});