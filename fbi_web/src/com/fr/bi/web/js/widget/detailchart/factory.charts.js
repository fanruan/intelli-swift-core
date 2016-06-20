BI.ChartCombineFormatItemFactory = {
    combineItems: function(types, items){
        var calItems = BI.values(items);
        return BI.map(calItems, function(idx, item){
            return BI.ChartCombineFormatItemFactory.formatItems(types[idx], item);
        });
    },
    formatItems: function(type, items){
        var item = {};
        switch (type) {
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
                item = BI.extend({"type": "bar"}, items);
                break;
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
                item = BI.extend({"type": "bubble"}, items);
                break;
            case BICst.WIDGET.SCATTER:
                item = BI.extend({"type": "scatter"}, items);
                break;
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
                item = BI.extend({"type": "column"}, items);
                break;
            case BICst.WIDGET.LINE:
                item = BI.extend({"type": "line"}, items);
                break;
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                item = BI.extend({"type": "area"}, items);
                break;
            case BICst.WIDGET.DONUT:
                item = BI.extend({"type": "pie"}, items);
                break;
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                item = BI.extend({"type": "radar"}, items);
                break;
            case BICst.WIDGET.PIE:
                item = BI.extend({"type": "pie"}, items);
                break;
            case BICst.WIDGET.DASHBOARD:
                item = BI.extend({"type": "gauge"}, items);
                break;
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                item = BI.extend({"type": "areaMap"}, items);
                break;
            default:
                item = BI.extend({"type": "column"}, items);
                break;
        }
        item.data = BI.map(item.data, function(idx, it){
            if(BI.has(it, "color") || BI.has(item, "color")){
                it.color = BI.has(it, "color") ? it.color : item.color;
            }
            if(BI.has(it, "borderColor") || BI.has(item, "borderColor")){
                it.borderColor = BI.has(it, "borderColor") ? it.borderColor : item.borderColor;
            }
            if(BI.has(it, "clickColor") || BI.has(item, "clickColor")){
                it.clickColor = BI.has(it, "clickColor") ? it.clickColor : item.clickColor;
            }
            if(BI.has(it, "mouseOverColor") || BI.has(item, "mouseOverColor")){
                it.mouseOverColor = BI.has(it, "mouseOverColor") ? it.mouseOverColor : item.mouseOverColor;
            }
            if(BI.has(it, "tooltip") || BI.has(item, "tooltip")){
                it.tooltip = BI.has(it, "tooltip") ? it.tooltip : item.tooltip;
            }
            if(BI.has(it, "dataLabels") || BI.has(item, "dataLabels")){
                it.dataLabels = BI.has(it, "dataLabels") ? it.dataLabels : item.dataLabels;
            }
            return it;
        });
        return item;
    },


    combineConfig: function(type){
        var config = {
            plotOptions:{
                marker:{
                    symbol:'circle'
                },

                dataLabels:{
                    enabled:true,
                    formatter:function(){return this.seriesName;}
                },

                tooltip:{
                    enabled:true,
                    formatter:function(){return this.seriesName;},
                    follow:false
                }
            },

            legend:{
                enabled:true
            },
            "chartType": "areaMap"
        };
        switch (type) {
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
                return BI.extend({"chartType": "bar"}, config);
            case BICst.WIDGET.BUBBLE:
                return BI.extend({"chartType": "bubble"}, config);
            case BICst.WIDGET.FORCE_BUBBLE:
                config.plotOptions.force = true;
                return BI.extend({"chartType": "bubble"}, config);
            case BICst.WIDGET.SCATTER:
                return BI.extend({"chartType": "scatter"}, config);
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
                return BI.extend({"chartType": "column"}, config);
            case BICst.WIDGET.LINE:
                return BI.extend({"chartType": "line"}, config);
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                return BI.extend({"chartType": "area"}, config);
            case BICst.WIDGET.DONUT:
                config.plotOptions.innerRadius = "50.0%";
                return BI.extend({"chartType": "pie"}, config);
            case BICst.WIDGET.RADAR:
                return BI.extend({"chartType": "radar"}, config);
            case BICst.WIDGET.ACCUMULATE_RADAR:
                config.plotOptions.columnType = true;
                return BI.extend({"chartType": "radar"}, config);
            case BICst.WIDGET.PIE:
                return BI.extend({"chartType": "pie"}, config);
            case BICst.WIDGET.DASHBOARD:
                delete config.dataSheet;
                return BI.extend({"chartType": "gauge"}, config);
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                return BI.extend({"chartType": "areaMap"}, config);
            default:
                return BI.extend({"chartType": "column"}, config);
        }
    }
};