/**
 * Created by fay on 2016/7/19.
 */
BI.DataLabelTab = BI.inherit(BI.Widget, {
    _constant: {
        TEXT_TOOL_BAR_HEIGHT: 60,
        IMAGE_SET_HEIGHT: 160
    },

    _defaultConfig: function () {
        var conf = BI.DataLabelTab.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {});
    },

    _init: function () {
        BI.DataLabelTab.superclass._init.apply(this, arguments);
        var self = this;
        var tab = BI.createWidget({
            type: "bi.button_group",
            items: [{
                type: "bi.radio",
                value: 1,
                handler: function () {
                    self.layout.setHeight(self._constant.TEXT_TOOL_BAR_HEIGHT);
                }
            }, {
                type: "bi.label",
                text: "文字标签",
                rgap: 20
            }, {
                type: "bi.radio",
                value: 2,
                handler: function () {
                    self.layout.setHeight(self._constant.IMAGE_SET_HEIGHT);
                }
            }, {
                type: "bi.label",
                text: "图片标签"
            }],
            layouts: [{
                type: "bi.left_vertical_adapt",
                items: [{
                    el: {
                        type: "bi.horizontal",
                        width: 550,
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
        this.layout = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                el: tab
            }],
            width: 538,
            height: 60,
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
        var self = this;
        this.texttoolbar = BI.createWidget({
            type: "bi.data_label_text_toolbar"
        });
        this.texttoolbar.on(BI.DataLabelTextToolBar.EVENT_CHANGE, function () {
            self._value = self.texttoolbar.getValue();
        });
        return BI.createWidget({
            type: "bi.absolute",
            cls: "",
            items: [{
                el: this.texttoolbar,
                top: 30,
                left: 0
            }]
        })
    },

    _createImageLabel: function () {
        var self = this, o = this.options;
        this.imageset = BI.createWidget({
            type: "bi.data_label_image_set"
        });
        this.imageset.setValue(this._value);
        this.imageset.on(BI.DataLabelImageSet.EVENT_CHANGE, function () {
            self._value = self.imageset.getValue();
            self.fireEvent(BI.DataLabelTab.IMG_CHANGE, arguments);
        });
        this.barchart = BI.createWidget({
            type: "bi.data_label_bar_chart"
        });
        this.barchart.on(BI.DataLabelImageSet.EVENT_CHANGE, function () {
        });
        this.barchart.populate();
        return BI.createWidget({
                type: "bi.absolute",
                items: [{
                    el: this.barchart,
                    top: 30
                }, {
                    el: this.imageset,
                    top: 30,
                    left: 150
                }],
                bgap: 10
            }
        );
    },
    setValue: function (v) {
        if (v.type === "img") {
            this._value = v;
        } else {
            this.texttoolbar.setValue(v);
        }
    },
    getValue: function () {
        return this._value;
    }
});
BI.DataLabelTab.IMG_CHANGE = "BI.DataLabelTab.IMG_CHANGE";
$.shortcut("bi.data_label_tab", BI.DataLabelTab);