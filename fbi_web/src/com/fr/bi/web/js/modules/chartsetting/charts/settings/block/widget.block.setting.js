/**
 * 样式面板组件设置区
 * Created by AstronautOO7 on 2016/10/12.
 */
BI.WidgetBlockSetting = BI.inherit(BI.Widget, {
    _defaultConfig: function() {
        return BI.extend(BI.WidgetBlockSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-widget-block-setting bi-charts-setting"
        })
    },

    _init: function() {
        BI.WidgetBlockSetting.superclass._init.apply(this, arguments);
        var self = this;

        //显示组件标题
        this.showTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Chart_Title"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.showTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.widgetTitle.setVisible(this.isSelected());
            self.fireEvent(BI.WidgetBlockSetting.EVENT_CHANGE);
        });

        //组件标题
        this.title = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.title.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.WidgetBlockSetting.EVENT_CHANGE)
        });

        //详细设置
        this.titleDetailSettting = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.titleDetailSettting.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.WidgetBlockSetting.EVENT_CHANGE)
        });

        this.widgetTitle = BI.createWidget({
            type: "bi.left",
            items: [this.title, this.titleDetailSettting],
            hgap: 10
        });

        //组件背景
        this.widgetBackground = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBackground.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.WidgetBlockSetting.EVENT_CHANGE);
        });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            cls: "single-line-settings",
            items: BI.createItems([{
                type: "bi.label",
                text: BI.i18nText("BI-Component_Widget"),
                cls: "line-title",
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Title"),
                cls: "line-title",
                lgap: 38
            }, {
                type: "bi.vertical_adapt",
                items: [this.showTitle]
            }, {
                type: "bi.vertical_adapt",
                items: [this.widgetTitle]
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Background"),
                cls: "line-title",
            }, {
                type: "bi.vertical_adapt",
                items: [this.widgetBackground]
            }], {
                height: 58
            }),
            hgap: 10
        })
    },

    populate: function() {
        var wId = this.options.wId;
        var wSetting = BI.Utils.getWSWidgetSettingByID(wId);
        this.showTitle.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetTitle.setVisible(BI.Utils.getWSShowNameByID(wId));
        this.title.setValue(BI.Utils.getWidgetNameByID(wId));
        this.titleDetailSettting.setValue(wSetting.title_detail);
        this.widgetBackground.setValue(wSetting.widget_bg);
    },

    getValue: function() {
        return {
            show_name: this.showTitle.isSelected(),
            widget_title: this.title.getValue(),
            title_detail: this.titleDetailSettting.getValue(),
            widget_bg: this.widgetBackground.getValue(),
        }
    },

    setValue: function(v) {
        this.showTitle.setSelected(v.show_name);
        this.title.setValue(v.widget_title);
        this.titleDetailSettting.setValue(v.title_detail);
        this.widgetBackground.setValue(v.widget_bg);
    }

});
BI.WidgetBlockSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.widget_block_setting", BI.WidgetBlockSetting);