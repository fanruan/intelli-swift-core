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
                    title:BI.i18nText("BI-SPA_Detail"),
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
        widget.view = widget.view || {};
        var dim1 = widget.view[BICst.REGION.DIMENSION1] || [];
        var dim2 = widget.view[BICst.REGION.DIMENSION2] || [];
        widget.view[BICst.REGION.DIMENSION1] = BI.concat(dim1, dim2);
        widget.view[BICst.REGION.DIMENSION2] = [];
        var usedDimensions = {}, hasUsed = false;
        var fields = [];
        if(BI.isNotNull(widget.view)) {
            BI.each([BICst.REGION.DIMENSION1], function (idx, item) {
                BI.each(widget.view[item], function (idx, id) {
                    var dimension = widget.dimensions[id];
                    if (dimension.used === true){
                        var field_type =  BI.Utils.getFieldTypeByID(dimension._src);
                        if (field_type === BICst.COLUMN.DATE
                            && dimension.group.type !== BICst.GROUP.YMD
                            && dimension.group.type !== BICst.GROUP.YMDHMS){
                            field_type = BICst.COLUMN.NUMBER;
                        } else if(field_type === BICst.COLUMN.NUMBER
                                && BI.isNotNull(dimension.group) && dimension.group.type !== BICst.GROUP.ID_GROUP) {
                            field_type = BICst.COLUMN.STRING;
                        }
                        if(BI.isNull(field_type)){
                            field_type = BICst.COLUMN.NUMBER;
                        }
                        fields.push({
                            field_name : dimension.name,
                            field_type : field_type
                        });
                        hasUsed = true;
                    }
                })
            })
            BI.each([BICst.REGION.TARGET1, BICst.REGION.TARGET2, BICst.REGION.TARGET3], function (idx, item) {
                BI.each(widget.view[item], function (idx, id) {
                    var dimension = widget.dimensions[id];
                    if (dimension.used === true){
                        fields.push({
                            field_name : dimension.name,
                            field_type : BICst.COLUMN.NUMBER
                        });
                        hasUsed = true;
                    }
                })
            })
        }
        if (hasUsed === true){
            var table = {
                value : BI.UUID(),
                table_name : widget.name,
                operator : widget,
                etlType : ETLCst.ETL_TYPE.SELECT_NONE_DATA
            }
            table[ETLCst.FIELDS] = fields;
            items.push(table);
        }
        model[BI.AnalysisETLMainModel.TAB] = {items:items};
        return model;
    }
})
$.shortcut("bi.data_style_tab_etl", BI.ETLDataStyleTab);