/**
 * 带有文字的进度条
 * Created by Young's on 2017/2/23.
 */
BI.ProgressTextBar = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return BI.extend(BI.ProgressTextBar.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-progress-text-bar",
            height: 28
        });
    },

    _init: function () {
        BI.ProgressTextBar.superclass._init.apply(this, arguments);
        var o = this.options;
        this.processor = BI.createWidget({
            type: "bi.label",
            cls: "bi-progress-text-bar-processor",
            width: "0%",
            height: o.height
        });
        this.value = 0;

        this.label = BI.createWidget({
            type: "bi.label",
            height: o.height
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.vertical",
                    items: [this.processor]
                },
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }, {
                el: this.label,
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }]
        })
    },

    setValue: function (process) {
        process > 100 && (process = 100);
        this.value = process;
        this.processor.element.width(process + "%");
    },

    getValue: function () {
        return this.value;
    },

    setText: function (text) {
        this.label.setText(text);
    }


});
$.shortcut("bi.progress_text_bar", BI.ProgressTextBar);