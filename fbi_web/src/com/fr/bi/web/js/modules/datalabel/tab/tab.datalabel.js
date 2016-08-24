/**
 * Created by fay on 2016/7/19.
 */
BI.DataLabelTab = BI.inherit(BI.Widget, {
    _constant: {
        TEXT_TOOL_BAR_HEIGHT: 100,
        IMAGE_SET_HEIGHT: 160
    },

    _defaultConfig: function () {
        var conf = BI.DataLabelTab.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {});
    },

    _init: function () {
        BI.DataLabelTab.superclass._init.apply(this, arguments);
        this._style = {
            type: 0,
            textStyle: {},
            imgStyle: {}
        };
        var self = this;
        var tab = BI.createWidget({
            type: "bi.button_group",
            items: [{
                type: "bi.radio",
                value: 1
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Text_Label"),
                rgap: 20
            }, {
                type: "bi.radio",
                value: 2
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Image_Label")
            }],
            layouts: [{
                type: "bi.left_vertical_adapt",
                items: [{
                    el: {
                        type: "bi.horizontal",
                        width: 530,
                        tgap: 5,
                        lgap: 10
                    }
                }]
            }]
        });
        this.tabs = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            element: this.element,
            tab: tab,
            cardCreator: BI.bind(this._createPanel, this)
        });
        this.tabs.setSelect(1);
        this.tabs.on(BI.Tab.EVENT_CHANGE, function () {
            if (this.getSelect() == 1) {
                self.layout.setHeight(self._constant.TEXT_TOOL_BAR_HEIGHT);
            } else {
                self.layout.setHeight(self._constant.IMAGE_SET_HEIGHT);
            }
        });
        this.layout = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                el: tab
            }],
            width: 530,
            height: this._constant.TEXT_TOOL_BAR_HEIGHT,
            scrollable: null,
            scrolly: false,
            scrollx: false
        })
    },

    _createPanel: function (v) {
        switch (v) {
            case 1:
                return this._createTextLabel();
            case 2:
                return this._createImageLabel();
        }
    },

    _createTextLabel: function () {
        var self = this, o = this.options;
        this.textToolbar = BI.createWidget({
            type: "bi.data_label_text_toolbar",
            chartType: o.chartType
        });
        this.textToolbar.on(BI.DataLabelTextToolBar.EVENT_CHANGE, function () {
            self._style.type = BICst.DATA_LABEL_STYLE_TYPE.TEXT;
            self._style.textStyle = self.textToolbar.getValue();
            self.fireEvent(BI.DataLabelTab.TEXT_CHANGE);
        });
        return BI.createWidget({
            type: "bi.vertical",
            items: [{
                el: this.textToolbar,
                tgap: 30
            }]
        })
    },

    _createImageLabel: function () {
        var self = this, o = this.options;
        this.imageSet = BI.createWidget({
            type: "bi.data_label_image_set"
        });
        this.imageSet.setValue(this._style.imgStyle);
        this.imageSet.on(BI.DataLabelImageSet.EVENT_CHANGE, function () {
            self._style.type = BICst.DATA_LABEL_STYLE_TYPE.IMG;
            self._style.imgStyle = self.imageSet.getValue();
            self.chart.populate(self.imageSet.getValue().src);
            self.fireEvent(BI.DataLabelTab.IMG_CHANGE, arguments);
        });
        this.chart = BI.createWidget({
            type: "bi.data_label_chart",
            chartType: o.chartType
        });
        this.chart.populate(this._style.imgStyle.src);
        return BI.createWidget({
                type: "bi.vertical",
                items: [BI.createWidget({
                    type: "bi.horizontal",
                    cls: "img-select",
                    items: [this.chart, this.imageSet],
                    scrollable: null,
                    scrolly: false,
                    scrollx: false
                })],
                tgap: 28
            }
        );
    },
    setValue: function (v) {
        this._style = v;
        this.textToolbar.setValue(v.textStyle);
    },
    getValue: function () {
        return this._style;
    }
});
BI.DataLabelTab.IMG_CHANGE = "BI.DataLabelTab.IMG_CHANGE";
BI.DataLabelTab.TEXT_CHANGE = "BI.DataLabelTab.TEXT_CHANGE";
$.shortcut("bi.data_label_tab", BI.DataLabelTab);