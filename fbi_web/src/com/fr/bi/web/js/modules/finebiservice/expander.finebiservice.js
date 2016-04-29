/**
 * 选择文本字段
 *
 * Created by GUY on 2015/11/10.
 * @class BI.FineBIServiceExpander
 * @extends BI.Widget
 */
BI.FineBIServiceExpander = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.FineBIServiceExpander.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-finebi-service-expander",
            title: {},
            items: []
        });
    },

    _init: function () {
        BI.FineBIServiceExpander.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.title = BI.createWidget(o.title, {
            type: "bi.icon_text_item",
            cls: "finebi-service-expander-title dimension-from-font",
            height: 30,
            disabled: true,
            textHgap: 5
        });
        this.button_group = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(o.items, {
                type: "bi.text_item",
                cls: "finebi-service-expander-item bi-list-item-select",
                height: 30,
                textLgap: 25,
                textRgap: 10
            }),
            layouts: [{
                type: "bi.vertical",
                hgap: 10
            }]
        });
        this.button_group.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            vgap: 15,
            items: [{
                type: "bi.vertical",
                items: [{
                    type: "bi.absolute",
                    height: 30,
                    items: [{
                        el: this.title,
                        left: 5,
                        right: 0,
                        top: 0,
                        bottom: 0
                    }]
                }, this.button_group]
            }]
        });
    },

    setValue: function (v) {
        this.button_group.setValue(v);
    },

    getValue: function () {
        return this.button_group.getValue();
    }
});
BI.FineBIServiceExpander.EVENT_CHANGE = "FineBIServiceExpander.EVENT_CHANGE";
$.shortcut('bi.finebi_service_expander', BI.FineBIServiceExpander);