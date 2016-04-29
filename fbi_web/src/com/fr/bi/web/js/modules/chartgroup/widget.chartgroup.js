/**
 * @class BI.SelectChartGroup
 * @extend BI.Widget
 * 选择图表类型组
 */
BI.SelectChartGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.SelectChartGroup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-type-group"
        })
    },

    _init: function(){
        BI.SelectChartGroup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.tableCombo = BI.createWidget({
            type: "bi.table_type_combo",
            width: 40,
            height: 34,
            items: [BICst.TABLE_TYPE]
        });
        this.tableCombo.on(BI.TableTypeCombo.EVENT_CHANGE, function(){
            self.chartGroup.setValue(BICst.Widget.NONE);
            self.fireEvent(BI.SelectChartGroup.EVENT_CHANGE);
        });
        this.tableCombo.setValue(BICst.Widget.NONE);
        this.chartGroup = BI.createWidget({
            type: "bi.button_group",
            scrollable: false,
            items: BI.createItems(BICst.CHART_TYPE, {
                type: "bi.icon_button",
                iconHeight: 25,
                iconWidth: 25,
                width: 40,
                height: 34,
                extraCls: "detail-chart-type"
            }),
            layouts: [{
                type: "bi.left",
                hgap: 3
            }]
        });
        this.chartGroup.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.tableCombo.setValue(BICst.Widget.NONE);
            self.fireEvent(BI.SelectChartGroup.EVENT_CHANGE);
        });
        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.tableCombo, this.chartGroup],
            vgap: 3,
            hgap: 3
        })
    },

    getValue: function(){
        if(this.tableCombo.isSelected()){
            return this.tableCombo.getValue();
        } else {
            return this.chartGroup.getValue()[0];
        }
    },

    setValue: function(v){
        this.tableCombo.setValue(v);
        this.chartGroup.setValue(v);
    }
});
BI.SelectChartGroup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.select_chart_group", BI.SelectChartGroup);