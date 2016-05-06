ConvertView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ConvertView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-etl-group-convert-view"
        })
    },

    _init: function () {
        ConvertView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var self = this;

        this.baseFieldCombo = BI.createWidget({
            type: "bi.text_value_combo"
        });

        this.selectFieldsDataPane = BI.createWidget({
            type: "bi.convert_select_fields_data_pane"
        });

        this.selectFieldsDataPane.on(BI.ConvertSelectFieldsDataPane.EVENT_LOADING, function(){
            if(!self.loadingMasker){
                self.loadingMasker = BI.createWidget({
                    type: "bi.loading_mask",
                    masker: self.west,
                    text: BI.i18nText("BI-Loading")
                })
            }
        });

        this.selectFieldsDataPane.on(BI.ConvertSelectFieldsDataPane.EVENT_LOADED, function(){
            self.loadingMasker.destroy();
            self.loadingMasker = null;
        });

        this.initialPane = BI.createWidget({
            type: "bi.convert_initial_fields_pane"
        });

        this.genFieldsPane = BI.createWidget({
            type : "bi.convert_gen_fields_pane"
        });

        this.selectFieldsDataPane.on(BI.ConvertSelectFieldsDataPane.EVENT_CHANGE, function(){
            self.genFieldsPane.populate([self.initialPane.getValue(), self.selectFieldsDataPane.getValue()["lc_values"]]);
        });

        this.initialPane.on(BI.ConvertSelectFieldsDataPane.EVENT_CHANGE, function(){
            self.genFieldsPane.populate([self.initialPane.getValue(), self.selectFieldsDataPane.getValue()["lc_values"]]);
        });

        this.west = BI.createWidget({
            type: "bi.division",
            width: 500,
            height: 550,
            cls: "convert-operator-pane",
            columns: 2,
            rows: 3,
            items:[{
                column : 0,
                row : 0,
                width: 1,
                height: 0.1,
                el : {
                    type: "bi.htape",
                    items:[{
                        type: "bi.label",
                        cls: "table-name-text-twenty",
                        text: BI.i18nText("BI-Sequence_Based_On"),
                        width: 112
                    },{
                        type: "bi.center_adapt",
                        items: [{
                            el: this.baseFieldCombo
                        }]
                    },{
                        type: "bi.label",
                        cls: "table-name-text-twenty",
                        text: BI.i18nText("BI-Recognize_Columns_Of_Generated"),
                        width: 140
                    }]
                }
            },{
                column : 1,
                row : 0,
                width: 0,
                height: 0.1,
                el: {
                    type: "bi.layout"
                }
            },{
                column : 0,
                row : 1,
                width: 0.5,
                height: 0.45,
                el: {
                    type: "bi.absolute",
                    items: [{
                        el: this.selectFieldsDataPane,
                        left: 10,
                        right: 5,
                        top: 5,
                        bottom: 10
                    }]
                }
            },{
                column : 1,
                row : 1,
                width: 0.5,
                height: 0.45,
                el: {
                    type: "bi.absolute",
                    items:[{
                        el: this.initialPane,
                        left: 5,
                        right: 10,
                        top: 5,
                        bottom: 10
                    }]
                }
            },{
                column : 0,
                row : 2,
                width: 1,
                height: 0.45,
                el: {
                    type: "bi.absolute",
                    items:[{
                        el: this.genFieldsPane,
                        left: 10,
                        right: 10,
                        top: 0,
                        bottom: 10
                    }]
                }
            },{
                column : 1,
                row : 2,
                width: 0,
                height: 0.45,
                el:{
                    type: "bi.layout"
                }
            }]
        });

        BI.createWidget({
            type: "bi.center_adapt",
            element: vessel,
            items: [this.west]
        });

        var table = BI.firstObject(Pool.tables);
        var fields = table.fields, fieldsName = [];
        fields = fields[0].concat(fields[1]);

        fieldsName = BI.map(fields, function(idx, field){
            return {
                value : field.field_name
            }
        });
        this.baseFieldCombo.populate(fieldsName);

        self.selectFieldsDataPane.populate({
            table: table,
            fields: fields,
            data: []
        });
        self.initialPane.populate(fields);
    }
});

ConvertModel = BI.inherit(BI.Model, {

});