BI.ChooseSheetButton = FR.extend(BI.BasicButton, {
    
    _defaultConfig : function () {
        return BI.extend(BI.ChooseSheetButton.superclass._defaultConfig.apply(this, arguments), {
            extraCls:"bi-analysis-etl-operator-merge-choose-sheet-button bi-list-item cursor-pointer",
            height:30,
            value:"test",
            text:"sheet1"
        })
    },
    
    _init : function () {
        BI.ChooseSheetButton.superclass._init.apply(this, arguments);
        var o = this.options;
        BI.createWidget({
            element : this.element,
            type:"bi.left_right_vertical_adapt",
            height: o.height,
            items :{
                left:[{
                    type:"bi.layout",
                    width:10,
                    height:1
                },{
                    type:"bi.label",
                    cls:"label",
                    text:o.text,
                    title: o.text
                }],
                right:[{
                    type:"bi.icon_button",
                    cls:"check-mark-font mark"
                },{
                    type:"bi.layout",
                    width:20,
                    height:1
                }]
            }
        })
    }
    
})
$.shortcut("bi.analysis_etl_choose_sheet_button", BI.ChooseSheetButton)