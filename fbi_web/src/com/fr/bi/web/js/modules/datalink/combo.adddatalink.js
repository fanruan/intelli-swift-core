/**
 * Created by Young's on 2016/3/18.
 */
BI.AddDataLinkCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.AddDataLinkCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-add-data-link-combo"
        })
    },

    _init: function(){
        BI.AddDataLinkCombo.superclass._init.apply(this, arguments);
        var self = this;
        var popup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.DATA_LINK_MANAGE.DATABASES, {
                type: "bi.single_select_item",
                height: 25
            }),
            chooseType: BI.Selection.None,
            layouts: [{
                type: "bi.vertical"
            }]
        });
        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            el: {
                type: "bi.button",
                text: BI.i18nText("BI-New_Add_Connection"),
                height: 28
            },
            popup: {
                el: popup,
                maxHeight: 300
            },
            width: 110
        });
        popup.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.combo.hideView();
        });
        this.combo.on(BI.Combo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.AddDataLinkCombo.EVENT_CHANGE, v);
        });
    },

    setValue: function(v){
        this.combo.setValue(v);
    },

    getValue: function(){
        return this.combo.getValue();
    }
});
BI.AddDataLinkCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.add_data_link_combo", BI.AddDataLinkCombo);