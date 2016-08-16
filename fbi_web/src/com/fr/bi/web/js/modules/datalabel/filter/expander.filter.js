/**
 * @class BI.DataLabelFilterExpander
 * @extend BI.AbstractFilterItem
 * 过滤树的一个expander节点
 */
BI.DataLabelFilterExpander = BI.inherit(BI.AbstractFilterItem, {

    _constant: {
        EXPANDER_WIDTH: 20
    },

    _defaultConfig: function () {
        var conf = BI.DataLabelFilterExpander.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-filter-expander",
            el: {},
            popup: {}
        })
    },

    _init: function () {
        BI.DataLabelFilterExpander.superclass._init.apply(this, arguments);
        this._initExpander();
        this._initConditionsView();
        this._initStyleView();
        BI.createWidget({
            type: "bi.horizontal",
            element: this.element,
            items: [this.expander, this.conditionsView, this.style]
        });
    },

    _initExpander: function () {
        var self = this, o = this.options;
        var value = o.value, text = "";
        if (value === BICst.FILTER_TYPE.AND) {
            text = BI.i18nText("BI-And");
        } else {
            text = BI.i18nText("BI-Or");
        }
        this.expander = BI.createWidget({
            type: "bi.text_button",
            cls: "condition-and-or",
            text: text,
            value: value,
            id: o.id,
            width: this._constant.EXPANDER_WIDTH,
            height: "100%"
        });
        this.expander.on(BI.Controller.EVENT_CHANGE, function (type) {
            arguments[2] = self;
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
    },

    _initConditionsView: function () {
        var self = this, popup = this.options.popup;
        BI.each(popup.items, function (id, item) {
            return BI.extend(item, {
                hideStyle: true
            })
        });
        console.log(popup);
        this.conditionsView = BI.createWidget(popup);
        this.conditionsView.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
    },

    _initStyleView: function (initData) {
        var self = this, o = this.options;
        //var chartType = BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(o.dId));
        this.style = BI.createWidget({
            type: "bi.data_label_style_set",
            height: "100%"
            //chartType: chartType
        });
        BI.isNotNull(initData) && this.style.setValue(initData);
    },

    getValue: function () {
        return {
            type: this.expander.getValue(),
            value: this.conditionsView.getValue(),
            style: this.style.getValue(),
            id: this.options.id
        };
    },

    setValue: function () {

    },

    populate: function (items) {
        this.conditionsView.populate.apply(this.conditionsView, arguments);
    }
});
$.shortcut("bi.data_label_filter_expander", BI.DataLabelFilterExpander);