/**
 * @class ProgressBarView
 * @extend BI.View
 */
ProgressBarView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ProgressBarView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-progress-bar bi-mvc-layout"
        })
    },

    _init: function () {
        ProgressBarView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        var progress = BI.createWidget({
            type: "bi.progress_bar",
            width: 300
        });
        BI.createWidget({
            type: "bi.center_adapt",
            element: vessel,
            items: [progress]
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: {
                    type: "bi.button",
                    height: 25,
                    text: "setValue(100)",
                    handler: function () {
                        progress.setValue(100);
                    }
                },
                left: 0,
                right: 0,
                bottom: 30
            }, {
                el: {
                    type: "bi.button",
                    height: 25,
                    text: "setValue(75)",
                    handler: function () {
                        progress.setValue(75);
                    }
                },
                left: 0,
                right: 0,
                bottom: 0
            }]
        })
    }
});

ProgressBarModel = BI.inherit(BI.Model, {});