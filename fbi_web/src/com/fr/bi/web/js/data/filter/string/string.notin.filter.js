;
!(function(){
    BI.StringNotInFilterValue = function(param){
        param = param || {};
        this.type = param.type || BI.Selection.Multi;
        this.valueSet = param.value || [];
    };
    BI.StringNotInFilterValue.prototype = {
        constructor: BI.StringNotInFilterValue,

        isStringNotIn: function(value){
            if(BI.isNull(this.valueSet) || BI.isEmptyArray(this.valueSet)){
                return false;
            }
            return this.isAllSelect() ? BI.contains(this.valueSet, value) : !BI.contains(this.valueSet, value);
        },

        isAllSelect: function(){
            return this.type === BI.Selection.All;
        },

        getFilterResult: function(array) {
            var self = this;
            return BI.filter(array, function(idx, val){
                return self.isStringNotIn(val);
            });
        }
    }
})();