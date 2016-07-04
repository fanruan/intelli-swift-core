/**
 * Created by Young's on 2016/5/5.
 */
BIShow.QueryModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIShow.QueryModel.superclass._defaultConfig.apply(this, arguments), {
            
        })    
    },
    
    _init: function(){
        BIShow.QueryModel.superclass._init.apply(this, arguments);
    } 
});