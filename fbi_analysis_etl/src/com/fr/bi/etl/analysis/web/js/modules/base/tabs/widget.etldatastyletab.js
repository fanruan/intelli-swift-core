/**
 * 带一个按钮的 styletabpane
 * @class BI.DataStyleTabPlugin
 * @extend BI.Widget
 * @type {*|void}
 */

BI.ETLDataStyleTab = BI.inherit(BI.DataStyleTab, {
    _init: function(){
        BI.ETLDataStyleTab.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        BI.createWidget({
            type:"bi.absolute",
            element:this.element,
            items:[{
                el:{
                    type : 'bi.left_pointer_button',
                    pointerWidth : ETLCst.ENTERBUTTON.POINTERWIDTH,
                    iconCls : "icon-add",
                    height : ETLCst.ENTERBUTTON.HEIGHT,
                    width : ETLCst.ENTERBUTTON.WIDTH,
                    text: BI.i18nText('BI-new_analysis_result_table'),
                    handler:function () {
                       BI.createWidget({
                           type : "bi.analysis_etl_main",
                           model : self._createMainModel(o.wId),
                           element:BI.Layers.create(ETLCst.ANALYSIS_LAYER, "body")
                       })
                    }
                },
                left:0,
                top:ETLCst.ENTERBUTTON.GAP,
                bottom:0,
                right:0
            }]
        })
    },


    _deleteView: function (id, view) {
        BI.each(view, function (i, item) {
            item.remove(id)
        })
    },
    _createMainModel : function (wId) {
        var self = this, model = {}, items = [];
        var widget = BI.Utils.getWidgetCalculationByID(wId);
        var usedDimensions = {}, hasUsed = false;
        var fields = [];
        BI.each(widget.dimensions, function (i, dimension) {
            if (dimension.used === true){
                usedDimensions[i] = dimension;
                var field_type =  BI.Utils.getFieldTypeByID(dimension._src);
                if (field_type === BICst.COLUMN.DATE && dimension.group.type !== BICst.GROUP.YMD){
                    field_type = BICst.COLUMN.NUMBER;
                }
                fields.push({
                    field_name : dimension.name,
                    field_id : dimension._src.field_id,
                    field_type : field_type
                });
                hasUsed = true;
            } else {
                self._deleteView(i, widget.view);
            }
        })
        if (hasUsed === true){
            widget.dimensions = usedDimensions;
            items.push({
                value : BI.UUID(),
                table_name : widget.name,
                fields : fields,
                operator : widget,
                etlType : ETLCst.ETL_TYPE.SELECT_NONE_DATA
            });
        }
        model[BI.AnalysisETLMainModel.TAB] = items;
        return model;
    }
})
$.shortcut("bi.data_style_tab_etl", BI.ETLDataStyleTab);