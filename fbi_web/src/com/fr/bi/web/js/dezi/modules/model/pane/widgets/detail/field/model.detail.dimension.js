/**
 * @class BIDezi.DetailDimensionModel
 * @extend BI.Model
 *
 */
BIDezi.DetailDimensionModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIDezi.DetailDimensionModel.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function(){
        BIDezi.DetailDimensionModel.superclass._init.apply(this, arguments);
    },

    change: function(changed){
        if(BI.has(changed, "name")){
            var name = this.get("name");
            var hyperlink = this.get("hyperlink");
            if(BI.isNotNull(hyperlink)){
                var expression = hyperlink.expression;
                hyperlink.expression = expression.replaceAll("\\$\\{.*\\}", "${"+ name +"}");
                this.set("hyperlink", hyperlink);
            }
        }

    },

    local: function(){
        if(this.has("valueChange")){
            var value = this.get("valueChange");
            this.set("value", value);
            return true;
        }
        return false;
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
            BIDezi.DetailDimensionModel.superclass.destroy.apply(this, arguments);
        }
    }
});