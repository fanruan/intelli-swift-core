/**
 * guy
 * @extends BI.Single
 * @type {*|void|Object}
 */
BI.Radio = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        var conf = BI.Radio.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-radio display-block",
            element: $("<input type='radio'/>"),
            selected: false,
            handler: BI.emptyFn
        })
    },

    _init: function () {
        BI.Radio.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.setSelected(this.options.selected);
        this.element.bind("click", function (e) {
            var v = e.target.checked;
            BI.defer(function () {
                e.target.checked = v;
            });
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.CLICK, self.getValue(), self);
            self.fireEvent(BI.Radio.EVENT_CHANGE, self.getValue(), self);
            e.stopPropagation();
        })
    },

    setSelected: function (v) {
        this.options.selected = v;
        if (v) {
            this.element.attr("checked", "checked");
        } else {
            this.element.removeAttr("checked");
        }
    },

    isSelected: function () {
        return this.element.attr("checked") == "checked";
    },

    setEnable: function (b) {
        BI.Radio.superclass.setEnable.apply(this, [b]);
        this.element[0].disabled = !b;
    }
});
BI.Radio.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.radio", BI.Radio);