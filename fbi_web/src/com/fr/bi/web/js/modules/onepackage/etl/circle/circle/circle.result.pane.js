/**
 * 自循环列最終顯示面板
 * @class BI.CircleResultPane
 * @extends BI.Widget
 */
BI.CircleResultPane = BI.inherit(BI.Widget, {

    constants: {
        buttonWidth: 90,
        buttonHeight: 30,
        CIRCLE_LABEL_WIDTH: 70,
        itemComboHeight: 50,
        gap: 10,
        centerHeight: 25,
        resultHeight: 23
    },

    _defaultConfig: function(){
        return BI.extend(BI.CircleResultPane.superclass._defaultConfig.apply(this, arguments),  {
            baseCls: "bi-circle-result-pane"
        });
    },

    _init: function(){
        BI.CircleResultPane.superclass._init.apply(this, arguments);

        var self = this, o = this.options;
        this.reConfig = BI.createWidget({
            type: "bi.button",
            value: BI.i18nText("BI-Reconfiguration"),
            width: this.constants.buttonWidth,
            height: this.constants.buttonHeight
        });

        this.reConfig.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.CircleResultPane.BUTTON_CONFIG_CLICK);
        });

        this.list = BI.createWidget({
            type: "bi.center",
            cls: "operator-result-list",
            height: this.constants.centerHeight
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.left_right_vertical_adapt",
                height: this.constants.itemComboHeight,
                items: {
                    left: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Structure_Result"),
                        textAlign: "left",
                        cls: "operator-label-text",
                        width: this.constants.CIRCLE_LABEL_WIDTH
                    }],
                    right: [this.reConfig]
                }
            }, this.list],
            hgap: this.constants.gap
        });
    },

    getValue: function(){
        return {
            floors: this.floors,
            divide_length: this.divide_length,
            id_field_name: this.id_field_name,
            parentid_field_name: this.parentid_field_name,
            field_length : this.field_length,
            showfields: this.showfields
        };
    },

    populate: function(items){
        this.floors = items.floors;
        this.divide_length = items.divide_length || "";
        this.id_field_name = items.id_field_name;
        this.parentid_field_name = items.parentid_field_name || "";
        this.field_length = items.field_length;
        this.showfields = items.showfields;
        this.list.empty();
        items = BI.map(items.floors, function(idx, item){
            return {
                value: item.name,
                title: item.name
            }
        });

        this.list.populate(BI.createItems(items, {
            type: "bi.label",
            cls: "result-label",
            height: this.constants.resultHeight,
            textHeight: this.constants.resultHeight
        }));
    }
});

BI.CircleResultPane.BUTTON_CONFIG_CLICK = "BUTTON_CONFIG_CLICK";
$.shortcut("bi.circle_result_pane", BI.CircleResultPane);