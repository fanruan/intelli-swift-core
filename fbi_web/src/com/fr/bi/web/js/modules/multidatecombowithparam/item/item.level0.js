/**
 * @class BI.SelectDateWidgetLevel0Item
 * @extends BI.Single
 */
BI.SelectDateWidgetLevel0Item = BI.inherit(BI.BasicButton, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectDateWidgetLevel0Item.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-date-widget-level0-item",
            height: 25,
            logic: {
                dynamic: false
            },
            hgap: 0,
            lgap: 0,
            rgap: 0
        })
    },

    _init: function () {
        BI.SelectDateWidgetLevel0Item.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.checkbox = BI.createWidget({
            type: "bi.checking_mark_node",
            selected: o.selected
        });
        this.text = BI.createWidget({
            type: "bi.label",
            cls: "list-item-text",
            textAlign: "left",
            whiteSpace: "nowrap",
            textHeight: o.height,
            height: o.height,
            hgap: o.hgap,
            rgap: o.rgap,
            text: o.text,
            keyword: o.keyword,
            value: o.value,
            py: o.py
        });
        this.checkbox.on(BI.Controller.EVENT_CHANGE, function(type){
            if(type ===  BI.Events.CLICK) {
                self.setSelected(self.isSelected());
            }
        });

        BI.createWidget(BI.extend({
            element: this.element
        }, BI.LogicFactory.createLogic("horizontal", BI.extend(o.logic, {
            items: BI.LogicFactory.createLogicItemsByDirection("left", {
                type: "bi.center_adapt",
                items: [this.checkbox],
                width: 23
            } ,this.text)
        }))));
    },

    doRedMark: function(){
        this.text.doRedMark.apply(this.text, arguments);
    },

    unRedMark: function(){
        this.text.unRedMark.apply(this.text, arguments);
    },

    doClick: function(){
        BI.SelectDateWidgetLevel0Item.superclass.doClick.apply(this, arguments);
        this.checkbox.setSelected(this.isSelected());
    },

    setSelected: function(v){
        BI.SelectDateWidgetLevel0Item.superclass.setSelected.apply(this, arguments);
        this.checkbox.setSelected(v);
    }
});

$.shortcut("bi.select_date_widget_level0_item", BI.SelectDateWidgetLevel0Item);