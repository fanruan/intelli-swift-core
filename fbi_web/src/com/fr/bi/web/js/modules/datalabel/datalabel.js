/**
 * 数据标签
 * Created by Fay on 2016/7/13.
 */
BI.DataLabel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabel.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-data-label",
            dId: ""
        });
    },

    _init: function () {
        BI.DataLabel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.condition = BI.createWidget({
            type: "bi.data_label_condition",
            dId: o.dId
        });
        BI.createWidget({
            type: "bi.vertical",
            items: [this.condition],
            element: this.element
        });
    },

    populate: function () {
        var o = this.options;
        // var conditions = BI.Utils.getDimensionFilterValueByID(o.dId);
        // if (BI.isNotEmptyObject(conditions)) {
        //     conditions = [conditions];
        // } else {
        //     conditions = [];
        // }
        // this.transformConditions2Tree(conditions);
        // this.filter.populate(conditions);
        this.condition.populate();
    },
    
    getValue: function () {
        return this.condition.getValue();
    },

    getStyle: function (data) {
        var conditions = this.condition.getValue();

    }
});

BI.DataLabel.EVENT_CHANGE = "BI.DataBabel.EVENT_CHANGE";
$.shortcut("bi.data_label", BI.DataLabel);
