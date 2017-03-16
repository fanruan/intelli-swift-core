BI.AnalysisETLOperatorFilterTable = FR.extend(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorFilterTable.superclass._defaultConfig.apply(this, arguments), {
            extraCls:"bi-analysis-etl-operator-filter-table"
        })
    },

    _init: function () {
        BI.AnalysisETLOperatorFilterTable.superclass._init.apply(this, arguments)
        this.content = BI.createWidget({
            type:"bi.button_group",
            element:this.element,
            items:this._createItems(),
            layouts: [{
                type: "bi.horizontal"
            }]
        })

    },

    _checkEmptyData: function(data){
        return BI.isNull(BI.find(data.value, function(idx, item){
            return !BI.isEmptyArray(BI.ETLFilterViewItemFactory.createViewItems(item, data.field_name, data.fieldItems));
        }));
    },

    _createItems : function () {
        var self = this;
        var data = this.options.items;
        var fieldItems = this.options.fieldItems;
        var items = [];
        BI.each(data, function (idx, item) {
            var combineItem = BI.extend(item, {fieldItems: fieldItems});
            if (self._checkEmptyData(combineItem)) {
                return;
            }
            items.push({
                type: ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_single_column",
                data: combineItem
            })
            if (idx !== data.length - 1) {
                items.push({
                    type: "bi.layout",
                    width: 10
                })
            }
        })

        return items;
    },
    
    populate : function (items, fieldItems) {
        this.options.items = items;
        this.options.fieldItems = fieldItems;
        this.content.populate(this._createItems())
    }

})


$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_table", BI.AnalysisETLOperatorFilterTable)