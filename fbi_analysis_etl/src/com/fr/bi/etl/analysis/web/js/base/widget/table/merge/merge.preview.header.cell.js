/**
 * 预览表
 *
 * Created by GUY on 2015/12/25.
 * @class BI.PreviewTableHeaderCell
 * @extends BI.Widget
 */
BI.AnalysisETLMergePreviewTableHeaderCell = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLMergePreviewTableHeaderCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-analysis-etl-merge-p-t-h-cell",
            text: "",
            merge:[],
            rename:false,
            height:40,
            validationChecker : BI.emptyFn,
            quitChecker: BI.emptyFn
        });
    },

    _init: function () {
        BI.AnalysisETLMergePreviewTableHeaderCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var nameWidget = BI.createWidget({
            type:"bi.analysis_etl_merge_p_t_h_button",
            merge:o.merge,
            text:o.text
        })
        var items = [{
            el:nameWidget
        }]
        if(o.rename === true) {
            var nameEditor = BI.createWidget({
                type: "bi.editor",
                height: o.height,
                value: o.text,
                validationChecker: o.validationChecker,
                quitChecker: o.quitChecker,
                mouseOut: false,
                allowBlank: false,
                watermark: "",
                errorText: function (v) {
                    if (v === "") {
                        return BI.i18nText("BI-Field_Name_Not_Null");
                    } else {
                        return BI.i18nText("BI-Field_Name_Already_Exist");
                    }
                },
            });
            BI.createWidget({
                type:"bi.absolute",
                element:nameWidget.element,
                items:[{
                    el :nameEditor,
                    left: 6,
                    right: 0,
                    top: 0,
                    bottom: 0
                }],

            })

            var  showEditor =  function () {
                nameEditor.visible();
                nameEditor.focus();
                nameEditor.selectAll();
            }
            nameWidget.on(BI.Controller.EVENT_CHANGE, function () {
                showEditor()
            })
            nameEditor.on(BI.Editor.EVENT_CONFIRM, function () {
                nameEditor.invisible();
                nameWidget.setText(nameEditor.getValue())
                self.fireEvent(BI.AnalysisETLMergePreviewTable.EVENT_RENAME, nameEditor.getValue());
            });
            nameEditor.invisible();
            var editorIcon = BI.createWidget({
                type: "bi.icon_button",
                cls: "edit-set-font",
                width: o.height,
                height: o.height,
                handler: function(){
                    showEditor();
                }
            });
            items.push({
                el:editorIcon,
                width:o.height
            })
        }
        BI.createWidget({
            type:"bi.htape",
            element: this.element,
            items:items
        })
    }
});

$.shortcut("bi.analysis_etl_merge_p_t_h_cell", BI.AnalysisETLMergePreviewTableHeaderCell);