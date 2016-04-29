BI.View.prototype.createView = function(url, modelData, viewData){
    return BI.Factory.createView(url, BIConf.Views.get(url), _.extend({},BIConf.Models.get(url),modelData),viewData||{}, this);
};