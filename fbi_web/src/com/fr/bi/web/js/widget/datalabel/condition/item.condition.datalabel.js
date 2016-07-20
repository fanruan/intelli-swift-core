/**
 * Created by lfhli on 2016/7/15.
 */
BI.DataLabelConditionItem = BI.inherit(BI.Widget,{
    _items: [{
        value: "自身"
    },{
        value: "文本类"
    },{
        value: "数值类"
    },{
        value: "时间类"
    }],

    _defaultConfig: function () {
        var conf = BI.DataLabelConditionItem.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "data-label-condition-item"
        });
    },
    
    _init: function () {
        BI.DataLabelConditionItem.superclass._init.apply(this, arguments);
        var self = this;
        this.condition = BI.createWidget({
            type: "bi.combo",
            el:{
                type: "bi.text_button",
                text: "请选择字段",
                cls: "condition-trigger",
                width: 80,
                height: 25
            },
            popup: {
                el: {
                    type: "bi.button_group",
                    items: BI.createItems(this._items, {
                        type: "bi.text_button",
                        height: 25,
                        handler: function (v) {
                            self.condition.hideView();
                            self.buttongroup.removeItemAt([1,2,3]);
                            self.buttongroup.addItems(self._initOthers());
                        }
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            }
        });

        this.buttongroup = BI.createWidget({
            type: "bi.button_group",
            items: [this.condition],
            element: this.element,
            height: 40,
            layouts: [{
                type: "bi.vertical_adapt",
                hgap: 8
            }]
        })
    },

    _initOthers: function () {
        var self = this;
        this.relation = BI.createWidget({
            type: "bi.text_value_down_list_combo",
            width: 120,
            height: 30,
            items: BICst.FILTER_DATE_COMBO
        });
        this.relation.setValue(BICst.FILTER_DATE.BELONG_DATE_RANGE);
        this.interval = BI.createWidget({
            type: "bi.numerical_interval",
            width: 200,
            height: 30
        });
        this.styleTab = BI.createWidget({
            type: "bi.data_label_tab"
        });
        this.styleTrigger = BI.createWidget({
            type: "bi.text_button",
            text: "设置样式",
            width: 80,
            height: 38,
            cls: "condition-trigger"
        });
        this.style = BI.createWidget({
            type: "bi.combo",
            isNeedAdjustWidth: false,
            el: this.styleTrigger,
            popup: {
                el: this.styleTab
            },
            offsetStyle: "right"
        });
        this.style.on(BI.Combo.EVENT_AFTER_HIDEVIEW, function () {
            this.setValue("text");
            //$(self.styleTrigger.element[0].childNodes[0].childNodes[0]).css(self.styleTab.getValue());
        });
        return [this.relation, this.interval, this.style];
    }
});

BI.DataLabelConditionItem.EVENT_CHANGE = "BI.DataLabelConditionItem.EVENT_CHANGE";
$.shortcut("bi.data_label_condition_item", BI.DataLabelConditionItem);