/**
 * 选择文本字段
 *
 * Created by GUY on 2015/11/10.
 * @class BI.FineBIService
 * @extends BI.Widget
 */

BI.FineBIService = BI.inherit(BI.Widget, {

    _const: {
        data: [{
            type: "bi.finebi_service_expander",
            title: {
                text: BI.i18nText("BI-PrePare_Data"),
                value: "BI-PrePare_Data"
            },
            items: [{
                text: BI.i18nText("BI-Data_Connection_Man"),
                value: "BI-Data_Connection_Man"
            }]
        }, {
            type: "bi.finebi_service_expander",
            title: {
                text: BI.i18nText("BI-Package_Data"),
                value: "BI-Package_Data"
            },
            items: [{
                text: BI.i18nText("BI-Packages_Man"),
                value: "BI-Packages_Man"
            }, {
                text: BI.i18nText("BI-Multi_Path_Man"),
                value: "BI-Multi_Path_Man"
            }]
        }, {
            type: "bi.finebi_service_expander",
            title: {
                text: BI.i18nText("BI-Permissions_Setting"),
                value: "BI-Permissions_Setting"
            },
            items: [{
                text: BI.i18nText("BI-Permissions_Man"),
                value: "BI-Permissions_Man"
            }]
        }, {
            type: "bi.finebi_service_expander",
            title: {
                text: BI.i18nText("BI-Cube_Information"),
                value: "BI-Cube_Information"
            },
            items: [{
                text: BI.i18nText("BI-Cube_Updates_Setting"),
                value: "BI-Cube_Updates_Setting"
            }]
        }]
    },

    _defaultConfig: function () {
        return BI.extend(BI.FineBIService.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-finebi-service"
        });
    },

    _init: function () {
        BI.FineBIService.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.button_tree = BI.createWidget({
            type: "bi.button_tree",
            element: this.element,
            items: this._createItems(),
            layouts: [{
                type: "bi.vertical"
            }]
        });
        this.button_tree.on(BI.ButtonTree.EVENT_CHANGE, function () {
            self.fireEvent(BI.FineBIService.EVENT_CHANGE, arguments);
        });
    },

    _createItems: function () {
        return this._const.data;
    },

    setValue: function (v) {
        this.button_tree.setValue(v);
    },

    getValue: function () {
        return this.button_tree.getValue();
    }
});
BI.FineBIService.EVENT_CHANGE = "FineBIService.EVENT_CHANGE";
$.shortcut('bi.finebi_service', BI.FineBIService);