/**
 * 形式为title + 一组left布局的button
 * @class BI.CircleTabRegion
 * @extends BI.Widget
 */

BI.CircleTabRegion = BI.inherit(BI.Widget, {

    constants: {
        titleHeight: 30
    },

    _defaultConfig: function() {
        return BI.extend(BI.CircleTabRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-circle-tab-region",
            items: [],
            title: ""
        })
    },

    _init : function() {
        BI.CircleTabRegion.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.group = BI.createWidget({
            type: "bi.button_group",
            items: o.items,
            layouts: [{
                type: "bi.left",
                hgap: 5,
                vgap: 5
            }]
        });

        this.group.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.fireEvent(BI.CircleTabRegion.EVENT_CHANGE);
        });

        this.group.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        if(!BI.isEmpty(o.title)){
            this.title = BI.createWidget({
                type: "bi.label",
                text: o.title,
                cls: "field-region-title",
                textAlign: "left",
                height: this.constants.titleHeight
            });
        }

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.title, this.group]
        });
    },

    populate: function(items){
        this.group.populate(BI.createItems(items, {
            type: "bi.text_button",
            cls: "field-region-button",
            hgap: 10
        }));
    },

    setValue: function(v){
        v = BI.isArray() ? v : [v];
        this.group.setValue(v);
    },

    getValue: function(){
        return this.group.getValue();
    }
});

BI.CircleTabRegion.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.circle_tab_region", BI.CircleTabRegion);