/**
 * BI风格
 *
 * Created by GUY on 2016/2/15.
 * @class FS.StyleSetting
 * @extends BI.Widget
 */
FS.StyleSetting = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(FS.StyleSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-fs-style-setting"
        });
    },

    _init: function () {
        FS.StyleSetting.superclass._init.apply(this, arguments);

        var style = this._createStyle();
        var color = this._createColor();

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [style, color]
        });
    },

    _createStyle: function () {
        var self = this;
        var label = BI.createWidget({
            type: "bi.label",
            height: 25,
            textAlign: "left",
            hgap: 10,
            text: BI.i18nText('BI-Total_Style')
        });

        this.style = BI.createWidget({
            type: "bi.text_value_combo",
            cls: "style-setting-combo",
            height: 25,
            text: BI.i18nText('BI-Common'),
            items: [{
                text: BI.i18nText('BI-Common'), value: 0
            }, {
                text: BI.i18nText('BI-Top_Down_Shade'), value: 1
            }]
        });
        this.style.on(BI.TextValueCombo.EVENT_CHANGE, function () {
            self._save();
        });
        this.style.setValue(0);

        return BI.createWidget({
            type: "bi.horizontal_adapt",
            height: 40,
            columnSize: [135, 260, ''],
            items: [label, this.style, BI.createWidget()]
        })
    },

    _createColor: function () {
        var self = this;
        var label = BI.createWidget({
            type: "bi.label",
            height: 25,
            textAlign: "left",
            hgap: 10,
            text: BI.i18nText('BI-Chart_Color')
        });

        this.color = BI.createWidget({
            type: "bi.text_value_combo",
            cls: "style-setting-combo",
            height: 25
        });
        this.color.on(BI.TextValueCombo.EVENT_CHANGE, function () {
            self._save();
        });

        return BI.createWidget({
            type: "bi.horizontal_adapt",
            height: 40,
            columnSize: [135, 260, ''],
            items: [label, this.color, BI.createWidget()]
        })
    },

    _save: function () {
        var chartStyle = this.style.getValue()[0] || 0;
        var defaultColor = this.color.getValue()[0];

        BI.requestAsync('fr_bi_base', 'set_config_setting', {
                chartStyle: chartStyle,
                defaultColor: defaultColor
            },
            function (res) {
                FR.Msg.toast(BI.i18nText("FS-Generic-Simple_Successfully"));
            }
        );
    },

    populate: function () {
        this.data = BI.requestSync('fr_bi_base', 'get_config_setting', null);
        this.color.populate(this.data.styleList);
        this.style.setValue(this.data.chartStyle || 0);
        if (BI.isKey(this.data.defaultColor)) {
            this.color.setValue(this.data.defaultColor);
        } else if (this.data.styleList.length > 0) {
            this.color.setValue(this.data.styleList[0].value);
        }
    }
});
$.shortcut('fs.style_setting', FS.StyleSetting);