;
!(function(){
    BI.EmptyFilter = function(){

    };
    BI.EmptyFilter.prototype = {
        constructor: BI.EmptyFilter,

        isQualified: function(){
            return true;
        },

        getFilterResult: function() {
            return true;
        }
    }
})();