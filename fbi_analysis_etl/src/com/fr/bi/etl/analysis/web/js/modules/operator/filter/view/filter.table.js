BI.AnalysisETLOperatorFilterTable = FR.extend(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorFilterTable.superclass._defaultConfig.apply(this, arguments), {
            extraCls:"bi-analysis-etl-operator-filter-table"
        })
    },

    _init: function () {
        BI.AnalysisETLOperatorFilterTable.superclass._init.apply(this, arguments)
        this.content = BI.createWidget({
            type:"bi.horizontal",
            element:this.element,
            items:this._createItems()
        })

    },

    _createItems : function () {
        var data = this.options.items;
        var fieldItems = this.options.fieldItems;
        var items = []
        BI.each(data, function(idx, item){
            items.push({
                type:ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_single_column",
                data:BI.extend(item, {fieldItems : fieldItems})
            })
            if(idx !== data.length - 1) {
                items.push({
                    type:"bi.layout",
                    width:10
                })
            }
        })

        return items;
    },
    
    populate : function (items, fieldItems) {
        this.content.empty();
        this.options.items = items;
        this.options.fieldItems = fieldItems;
        this.content.populate(this._createItems())
    }

})


$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_table", BI.AnalysisETLOperatorFilterTable)