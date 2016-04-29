/**
 * Cube日志错误Item标题
 *
 * Created by GUY on 2016/4/1.
 * @class BI.CubeLogWrongInfoItemTitle
 * @extends BI.Widget
 */
BI.CubeLogWrongInfoItemTitle = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeLogWrongInfoItemTitle.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log-wrong-info-item-title",
            text: "关联: 以下表的关联关系造成死循环",
            height: 30,
            open: true
        });
    },

    _init: function () {
        BI.CubeLogWrongInfoItemTitle.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.switcher = BI.createWidget({
            type: "bi.text_button",
            text: o.open === true ? "点击收起" : "点击展开",
            height: o.height,
            cls: "cube-log-wrong-info-item-title-switcher"
        });
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                type: "bi.label",
                textAlign: "left",
                lgap: 10,
                text: o.text,
                height: o.height
            }, {
                el: this.switcher,
                width: 50
            }]
        });

        this.switcher.on(BI.TextButton.EVENT_CHANGE, function () {
            self.setOpened(!self.isOpened());
            self.fireEvent(BI.CubeLogWrongInfoItemTitle.EVENT_CHANGE, arguments);
        });
    },

    populate: function () {

    },

    isOpened: function () {
        return this.options.open;
    },

    setOpened: function (v) {
        var o = this.options;
        o.open = v;
        this.switcher.setText(o.open === true ? "点击收起" : "点击展开");
    }
});
BI.CubeLogWrongInfoItemTitle.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.cube_log_wrong_info_item_title', BI.CubeLogWrongInfoItemTitle);