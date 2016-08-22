;
!(function(){
    BI.StringNotInFilterValue = function(type, valueSet){
        this.type = type;
        this.valueSet = valueSet;
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

        isQualified: function(value){
            return this.isAllSelect() ? !this.isStringNotIn(value) : this.isStringNotIn(value);
        }
    }
})();