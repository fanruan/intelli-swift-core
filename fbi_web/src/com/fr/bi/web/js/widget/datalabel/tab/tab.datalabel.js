/**
 * Created by fay on 2016/7/19.
 */
BI.DataLabelTab = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabelTab.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {

        });
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
                    self.layout.setHeight(60);
                }
            },{
                type: "bi.label",
                text: "文字标签",
                rgap: 20
            },{
                type: "bi.radio",
                value: 2,
                handler: function () {
                    self.layout.setHeight(160);
                }
            },{
                type: "bi.label",
                text: "图片标签"
            }],
            layouts: [{
                type: "bi.left_vertical_adapt",
                items: [{
                    el: {
                        type: "bi.horizontal",
                        width: 550,
                        lgap: 2
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
            width: 560,
            height: 60,
            scrollable: null,
            scrolly: false,
            scrollx: false
        })
    },

    _createPanel: function (v) {
        switch (v){
            case 1:
                return this._createTextLabel();
            case 2:
                return this._createImageLabel();
        }
    },

    _createTextLabel: function () {
        this.texttoolbar = BI.createWidget({
            type: "bi.data_label_text_toolbar"
        });
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: this.texttoolbar,
                top: 30,
                left: 0
            }]
        })
    },

    _createImageLabel: function () {
        this.imageset = BI.createWidget({
            type: "bi.data_label_image_set"
        });
        this.barchart = BI.createWidget({
            type: "bi.data_label_bar_chart"
        });
        return BI.createWidget({
                type: "bi.absolute",
                items: [{
                    el: this.barchart,
                    top: 30
                },{
                    el: this.imageset,
                    top: 30,
                    left: 150
                }],
                bgap: 10
            }
        );
    },
});

$.shortcut("bi.data_label_tab", BI.DataLabelTab);