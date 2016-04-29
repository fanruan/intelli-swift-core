/**
 * Cube日志
 *
 * Created by GUY on 2016/4/6.
 * @class BI.CubeLog
 * @extends BI.Widget
 */
BI.CubeLog = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeLog.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log"
        });
    },

    _init: function () {
        BI.CubeLog.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var cubeTree = BI.createWidget({
            type: "bi.cube_log_tree"
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.left_right_vertical_adapt",
                cls: "refresh-bar",
                items: {
                    left: [{
                        type: "bi.icon_button",
                        cls: "task-list-font" + " task-list-comment-font",
                        width: 30,
                        height: 30
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Update_Task_List"),
                        height: 30,
                        cls: "task-list-comment-label"
                    }],
                    right: [{
                        type: "bi.button",
                        text: BI.i18nText("BI-Refresh"),
                        height: 28,
                        level: "ignore"
                    }]
                },
                height: 50
            }, cubeTree]
        });
        BI.Utils.getCubeLog(function(){

        })
    },

    populate: function () {

    }
});
$.shortcut('bi.cube_log', BI.CubeLog);