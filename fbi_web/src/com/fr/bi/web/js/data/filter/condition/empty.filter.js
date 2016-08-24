;
!(function(){
    BI.EmptyFilter = function(){

    };
    BI.EmptyFilter.prototype = {
        constructor: BI.EmptyFilter,

        isQualified: function(){
            return false;
        },

        getFilterResult: function() {
            return [];
        }
    }
})();