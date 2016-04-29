/**
 * 选择字段trigger小一号的
 *
 * @class BI.SmallSelectTextTrigger
 * @extends BI.Trigger
 */
BI.SmallSelectTextTrigger = BI.inherit(BI.Trigger, {

    _defaultConfig: function () {
        return BI.extend(BI.SmallSelectTextTrigger.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-small-select-text-trigger",
            height: 20
        });
    },

    _init: function () {
        BI.SmallSelectTextTrigger.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget({
            type: "bi.small_text_trigger",
            element: this.element,
            height: o.height - 2,
            text: o.text
        });
    },

    setValue: function (vals) {
        vals = BI.isArray(vals) ? vals : [vals];
        var result = [];
        BI.each(this.options.items, function (i, item) {
            if (vals.contains(item.value)) {
                result.push(item.text || item.value);
            }
        });
        this.trigger.setText(result.join(","));
    },

    populate: function (items) {
        this.options.items = items;
    }
});
$.shortcut("bi.small_select_text_trigger", BI.SmallSelectTextTrigger);