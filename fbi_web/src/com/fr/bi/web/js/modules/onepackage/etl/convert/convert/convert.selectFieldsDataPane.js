/**
 * 选择栏次面板
 * @class BI.ConvertSelectFieldsDataPane
 * @extends BI.Widget
 */
BI.ConvertSelectFieldsDataPane = BI.inherit(BI.Widget, {

    constants: {
        COMBO_HEIGHT: 25,
        itemHeight: 25,
        triggerHeight:25
    },

    _defaultConfig: function(){
        return BI.extend(BI.ConvertSelectFieldsDataPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-convert-select-fields-data-pane"
        });
    },

    _init: function(){
        BI.ConvertSelectFieldsDataPane.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.combo = BI.createWidget({
            type: "bi.text_value_combo",
            height:this.constants.triggerHeight
        });

        this.button_tree = BI.createWidget({
            type:"bi.button_tree",
            chooseType: BI.Selection.Multi,
            layouts: [{
                type: "bi.vertical",
                vgap: 5,
                hgap: 5
            }]
        });

        this.combo.on(BI.Combo.EVENT_CHANGE, function(){
            var field = BI.find(o.fields, function(idx, field){
                return field["field_name"] == self.combo.getValue()[0]
            });
            self.fireEvent(BI.ConvertSelectFieldsDataPane.EVENT_LOADING);
            BI.Utils.getConfDataByField(o.table, BI.isNull(field) ? null : field.field_name, {
                type: BICst.REQ_DATA_TYPE.REQ_GET_ALL_DATA
            }, function(data){
                self.button_tree.populate(self._createItemsByData(data));
                self.fireEvent(BI.ConvertSelectFieldsDataPane.EVENT_LOADED);
                self.fireEvent(BI.ConvertSelectFieldsDataPane.EVENT_CHANGE);
            });
        });

        this.button_tree.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.ConvertSelectFieldsDataPane.EVENT_CHANGE);
        });

        BI.createWidget({
            type: "bi.panel",
            element: this.element,
            title: BI.i18nText("BI-Select_Junior_Name"),
            el:{
                type: "bi.vtape",
                items: [{
                    el: this.combo,
                    height: this.constants.COMBO_HEIGHT
                },{
                    el: this.button_tree
                }]
            }
        });
    },

    _createItemsByData: function(items){
        items = BI.map(items, function(idx,item){
            return {
                value: item
            }
        });
        return BI.createItems(items, {
            type: "bi.convert_multi_select_item",
            selected: true,
            height: this.constants.itemHeight
        });
    },

    _createItemsByFields: function(fields){
        return BI.map(fields, function(idx, field){
            return {
                value : field["field_name"]
            }
        });
    },

    getValue: function(){

        var results = [];
        BI.each(this.button_tree.getValue(), function(i, val){
            if(val.selected === true){
                results.push(val.value);
            }
        });

        return {
            lc_name: this.combo.getValue()[0],
            lc_values: results
        };
    },

    setValue: function(v) {
        this.combo.setValue(v.lc_name);

        var self = this;
        var values = BI.map(v.lc_values, function (idx, value) {
            return {
                value: value,
                selected: true
            }
        });
        self.button_tree.setValue(values);
    },

    populate: function(items){
        this.options.table = items.table;
        this.options.fields = items.fields;
        this.combo.populate(this._createItemsByFields(items.fields));
        this.button_tree.populate(this._createItemsByData(items.data));
    }
});

BI.ConvertSelectFieldsDataPane.EVENT_LOADING = "EVENT_LOADING";
BI.ConvertSelectFieldsDataPane.EVENT_LOADED = "EVENT_LOADED";
BI.ConvertSelectFieldsDataPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.convert_select_fields_data_pane", BI.ConvertSelectFieldsDataPane);