/**
 * 为settingpane的标题交互特意搞的，我是觉得没必要了
 * @class BI.TargetLabelControl
 * @extends BI.Widget
 */
BI.TargetLabelControl = BI.inherit(BI.Widget, {

    constants: {
        targetWidth: 45,
        font_size: 14
    },

    _defaultConfig: function () {
        return BI.extend(BI.TargetLabelControl.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-label-control"
        });
    },

    _init: function () {
        BI.TargetLabelControl.superclass._init.apply(this, arguments);
        var o = this.options;

        this.line = BI.createWidget({
            type: "bi.htape",
            cls: "target-region",
            items: []
        });

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                type: "bi.label",
                textAlign: "left",
                text: BI.i18nText("BI-Target"),
                cls: "setting-tip-label",
                width: this.constants.targetWidth
            }, this.line]
        });
    },

    populate: function(targetIds){
        var o = this.options;
        var text = "", tableName = "";
        BI.each(targetIds, function(idx, tId){
            if(idx === BI.size(targetIds) - 1){
                text += BI.Utils.getDimensionNameByID(tId);
                tableName = BI.Utils.getTableNameByID(BI.Utils.getTableIDByDimensionID(tId));
            }else {
                text += BI.Utils.getDimensionNameByID(tId) + BI.i18nText("BI-Pause_Sign")
            }
        });

        this.line.empty();
        this.line.populate([{
            el: {
                type: "bi.label",
                text: text,
                title: text + "("+ tableName + ")",
                cls: "target-name-label"
            },
            width: BI.DOM.getTextSizeWidth(text, this.constants.font_size)
        }, {
            type: "bi.label",
            title: text + "("+ tableName + ")",
            text: "("+ tableName + ")",
            textAlign: "left",
            cls: "table-name-label"
        }]);
    }
});
BI.TargetLabelControl.EVENT_CHANGE = "TargetLabelControl.EVENT_CHANGE";
$.shortcut('bi.target_label_control', BI.TargetLabelControl);