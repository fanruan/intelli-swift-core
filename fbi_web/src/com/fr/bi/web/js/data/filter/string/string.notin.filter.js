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
                return true;
            }
            return !BI.contains(this.valueSet, value);
        },

        isAllSelect: function(){
            return this.type === BI.Selection.All;
        },

        getFilterResult: function(array) {
            return BI.filter(array, function(idx, val){
                return this.isAllSelect() ? !this.isStringNotIn(val) : this.isStringNotIn(val);
            });
        }
    }
})();