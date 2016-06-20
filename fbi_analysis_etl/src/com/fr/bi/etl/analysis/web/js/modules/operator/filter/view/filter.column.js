BI.AnalysisETLOperatorFilterSingleColumn = FR.extend(BI.Widget, {

    _constant : {
        gapWidth:10,
        contentWidth:200
    },

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorFilterSingleColumn.superclass._defaultConfig.apply(this, arguments), {
            extraCls:"bi-analysis-etl-operator-filter-single-column",
            height:200
        })
    },

    _init: function () {
        BI.AnalysisETLOperatorFilterSingleColumn.superclass._init.apply(this, arguments)
        var o = this.options;

        this.title = BI.createWidget({
            type:"bi.label",
            cls:"title",
            text: o.data.field_name
        })

        BI.createWidget({
            type:"bi.vertical",
            element:this.element,
            items :[{
                type:"bi.center_adapt",
                items :[{
                    type:"bi.vertical_adapt",
                    items :[{
                        type:"bi.layout",
                        width:10,
                    }, {
                        el:this.title
                    }]
                }],
                height:30
            },{
                el :{
                    type:"bi.vertical_adapt",
                    items:this._createValueItems(o.data)
                },
                height:this.options.height - 30 - 10
            }]
        })
        var self = this;

    },



    _createValueItems : function (data) {
        var items = [];
        var contentHeight = this.options.height - 30 - 10;
        var text = data.type !== BICst.FILTER_TYPE.OR ? BI.i18nText("BI-And") : BI.i18nText("BI-Or")
        var self = this;
        BI.each(data.value, function(idx, item){
            items.push({
                type:"bi.layout",
                width:self._constant.gapWidth
            })
            items.push({
                type:ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_single_content",
                data:BI.ETLFilterViewItemFactory.createViewItems(item, data.field_name, data.fieldItems),
                height:contentHeight,
                width:self._constant.contentWidth
            })
            items.push({
                type:"bi.layout",
                width:self._constant.gapWidth
            })
            if(idx !== data.value.length - 1){
                items.push({
                    type:"bi.label",
                    cls:"operator",
                    text:text
                })
            }
        })
        return items;
    }

})


$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_single_column", BI.AnalysisETLOperatorFilterSingleColumn)