/**
 * 预览表
 *
 * Created by GUY on 2015/12/25.
 * @class BI.PreviewTableHeaderCell
 * @extends BI.Widget
 */
BI.AnalysisETLPreviewTableHeaderDeleteCell = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLPreviewTableHeaderDeleteCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-analysis-etl-p-t-h-cell",
            text: "",
            height:30,
            nameValidationChecker : BI.emptyFn()
        });
    },

    _init: function () {
        BI.AnalysisETLPreviewTableHeaderDeleteCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var editor = BI.createWidget({
            type: "bi.sign_editor",
            height: o.height,
            value: o.text,
            allowBlank: false,
            errorText: function (v) {
                if (v === "") {
                    return BI.i18nText("BI-Report_Name_Not_Null");
                } else {
                    return BI.i18nText("BI-Template_Name_Already_Exist");
                }
            },
            validationChecker: function (v) {
                if (v.indexOf("\'") > -1) {
                    return false;
                }
                return o.nameValidationChecker.apply(this, [v]);
            }
        })
        editor.on(BI.SignEditor.EVENT_VALID, function () {
            self.fireEvent(BI.AnalysisETLPreviewTable.EVENT_RENAME, arguments)
        });
        var deleteButton =  BI.createWidget({
            type:"bi.icon_button",
            cls:"delete-field-font bi-shake-icon",
            height: o.height,
            width: o.height,
            handler : function () {
                self.fireEvent(BI.AnalysisETLPreviewTable.DELETE_EVENT, arguments)
            }
        })
        BI.createWidget({
            type:"bi.htape",
            element: this.element,
            items:[{
                el : {
                    type:"bi.icon_button",
                    cls:BI.Utils.getFieldClass(o.field_type),
                    forceNotSelected :true,
                    height: o.height,
                    width: o.height
                },
                width: o.height
            }, {
                el: editor
            }, {
                el :deleteButton,
                width:o.height
            }]
        })
    }
});


$.shortcut(ETLCst.ANALYSIS_TABLE_OPERATOR_PREVIEW_HEADER + BI.ANALYSIS_ETL_HEADER.DELETE, BI.AnalysisETLPreviewTableHeaderDeleteCell);