

BI.ColumnButton = FR.extend(BI.BasicButton, {
    _defaultConfig : function () {
        return BI.extend(BI.ColumnButton.superclass._defaultConfig.apply(this, arguments), {
            baseCls:"bi-analysis-etl-operator-button-column",
            height:25,
            field_type:BICst.COLUMN.STRING,
            field_name:"aaa",
        })
    },

    _initOpts : function () {
        var o = this.options;
        o.title = o.field_name
    },
    
    _init : function () {
        BI.ColumnButton.superclass._init.apply(this, arguments);
        var o = this.options;
        var self = this;
        BI.createWidget({
            type:"bi.htape",
            element:this.element,
            items:[{
                type:"bi.icon_button",
                cls: BI.Utils.getFieldClass(o.field_type),
                width:o.height,
                height:o.height,
                forceNotSelected :true
            }, {
                el:{
                    // type:"bi.center_adapt",
                    height:o.height,
                    // items:[{
                        type:"bi.label",
                        textAlign:"left",
                        text:o.field_name
                    // }]
                }
            }, {
                type:"bi.icon_button",
                cls: "detail-dimension-set-font set",
                width:o.height,
                height:o.height,
                handler : function () {
                    self.fireEvent(BI.ColumnButton.EVENT_EDIT, o.field_name)
                }
            }, {
                type:"bi.icon_button",
                cls: "close-font delete",
                width:o.height,
                height:o.height,
                handler : function () {
                    self.fireEvent(BI.ColumnButton.EVENT_DELETE, o.field_name)
                }
            }]
        })
    }

})
BI.ColumnButton.EVENT_DELETE="event_delete";
BI.ColumnButton.EVENT_EDIT="event_edit";
$.shortcut("bi.etl_button_column", BI.ColumnButton);