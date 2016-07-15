/**
 * 数据标签
 * Created by Fay on 2016/7/13.
 */
BI.DataLabel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabel.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {

        });
    },

    _init: function () {
        BI.DataLabel.superclass._init.apply(this, arguments);
        var addButton = BI.createWidget({
            type: "bi.button",
            text: "添加条件",
            width: 80,
            handler: function () {
                
            }
        });
        this._createTab();
        BI.createWidget({
            type: "bi.vertical",
            items: [{
                type: "bi.right",
                items: [addButton],
                rgap: 10
            },this._createTab()],
            width: 560,
            element: this.element,
            height: 400
        })
    },

    _createTab: function () {
        var tab = BI.createWidget({
            type: "bi.button_group",
            items: [{
                type: "bi.radio",
                value: 1
            },{
                type: "bi.label",
                text: "文字标签",
                rgap: 20
            },{
                type: "bi.radio",
                value: 2
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
                        height: 30,
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
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: tab
            }]
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
                top: 50
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
                left: 0,
                top: 50
            },{
                el: this.imageset,
                left: 150,
                top: 50
            }],
            width: 550,
            height: 130
        })
    }
});

BI.DataLabel.EVENT_CHANGE = "BI.DataBabel.EVENT_CHANGE";
$.shortcut("bi.data_label", BI.DataLabel);
