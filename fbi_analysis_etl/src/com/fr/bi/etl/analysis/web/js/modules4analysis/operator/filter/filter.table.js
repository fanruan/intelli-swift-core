/**
 * Created by windy on 2017/4/6.
 */
BI.AnalysisETLOperatorFilterTable = BI.inherit(BI.Widget, {

    props: {
        extraCls:"bi-analysis-etl-operator-filter-table"
    },

    render: function(){
        var self = this, o = this.options;
        this.content = null;
        return {
            type:"bi.button_group",
            items:this._createItems(),
            ref: function(_ref){
                self.content = _ref;
            },
            layouts: [{
                type: "bi.horizontal"
            }]
        }
    },

    _checkEmptyData: function(data){
        return BI.isNull(BI.find(data.value, function(idx, item){
            return !BI.isEmptyArray(BI.ETLFilterViewItemFactory.createViewItems(item, data.fieldName, data.fieldItems));
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

});


BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_table", BI.AnalysisETLOperatorFilterTable)