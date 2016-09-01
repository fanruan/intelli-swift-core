/**
 * Created by fay on 2016/9/1.
 */
BI.DataImagePane = BI.inherit(BI.Widget, {
    _constant: {
        DEFAULT_TEXT_TOOL_BAR_HEIGHT: 70,
        TEXT_TOOL_BAR_HEIGHT: 100,
        IMAGE_SET_HEIGHT: 160
    },

    _defaultConfig: function () {
        var conf = BI.DataImagePane.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {});
    },

    _init: function () {
        BI.DataImagePane.superclass._init.apply(this, arguments);
        this._createImage();

        BI.createWidget({
                type: "bi.vertical",
                items: [BI.createWidget({
                    type: "bi.horizontal",
                    element: this.element,
                    items: [this.chart, this.imageSet],
                    scrollable: null,
                    scrolly: false,
                    scrollx: false
                })]
            }
        );
    },

    _createImage: function () {
        var self = this, o = this.options;
        this.imageSet = BI.createWidget({
            type: "bi.data_label_image_set"
        });
        this.imageSet.on(BI.DataLabelImageSet.EVENT_CHANGE, function () {
            self.chart.populate(self.imageSet.getValue().src);
            self.fireEvent(BI.DataImagePane.IMG_CHANGE, arguments);
        });
        this.chart = BI.createWidget({
            type: "bi.data_label_chart",
            chartType: o.chartType
        });
        this.chart.populate();
    },
    setValue: function (v) {
    },
    getValue: function () {
        return this.imageSet.getValue();
    }
});
BI.DataImagePane.IMG_CHANGE = "BI.DataImageTab.IMG_CHANGE";
$.shortcut("bi.data_image_pane", BI.DataImagePane);