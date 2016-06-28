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
        if(this.has("valueChange")){model.detail
            var value = this.get("valueChange");
            this.set("value", value);
            return true;
        }
        return false;
    }
});