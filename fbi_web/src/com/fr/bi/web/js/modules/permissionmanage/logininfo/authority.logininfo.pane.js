/**
 * Created by Young's on 2016/5/16.
 */
BI.AuthorityLoginInfoPane = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend(BI.AuthorityLoginInfoPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-login-info"
        })
    },

    _init: function(){
        BI.AuthorityLoginInfoPane.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function(north){
        var self = this;
        var reselect = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Reselect_Table"),
            height: 50,
            cls: "bi-authority-login-info-reselect-table"
        });
        reselect.on(BI.TextButton.EVENT_CHANGE, function(){
             self._reselectTable();
        });
        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            element: north,
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Select_Field_Of_Login_UserName"),
                    height: 50,
                    cls: ""
                }],
                right: [reselect]
            }
        });
    },

    rebuildCenter: function(center){
        var self = this, o = this.options;
        var fieldName = o.field_name;
        this.table = o.table;
        this.tableName = BI.createWidget({
            type: "bi.label",
            text: "",
            height: 30,
            cls: "comments-title"
        });
        this.tablesGroup = BI.createWidget({
            type: "bi.button_group",
            layouts: [{
                type: "bi.left",
                hgap: 5,
                vgap: 5
            }]
        });
        this.tablesGroup.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.fieldName.setValue(this.getValue()); 
        });
        this.fieldName = BI.createWidget({
            type: "bi.circle_select_field_button",
            height: 30
        });
        BI.createWidget({
            type: "bi.vertical",
            element: center,
            cls: "bi-authority-login-info",
            items: [{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-The_Field_Of_Username_In_Table"),
                    height: 30,
                    cls: "comments-title"
                }, this.tableName, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Zhongde"),
                    height: 30,
                    cls: "comments-title"
                }, this.fieldName]
            }, {
                type: "bi.left",
                items: [this.tablesGroup],
                cls: "fields-group",
                vgap: 15,
                hgap: 10
            }],
            hgap: 10,
            vgap: 10
        });
        this.populate(fieldName);
    },

    rebuildSouth: function(south){
        var self = this, o = this.options;
        this.clearButton = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "widget-combo-clear",
            text: BI.i18nText("BI-Clear_The_Setting"),
            value: BI.i18nText("BI-Clear_The_Setting"),
            height: 30,
            width: 100
        });
        this.clearButton.on(BI.IconTextItem.EVENT_CHANGE, function(){
            self.fireEvent(BI.AuthorityLoginInfoPane.EVENT_CLEAR); 
        });
        
        var cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Cancel"),
            height: 30
        });
        cancelButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.AuthorityLoginInfoPane.EVENT_CANCEL); 
        });
        
        var saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Save"),
            height: 30
        });
        saveButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.AuthorityLoginInfoPane.EVENT_SAVE); 
        });
        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            element: south,
            items: {
                left: [this.clearButton],
                right: [cancelButton, saveButton]
            },
            rlgap: 10
        });
    },

    _reselectTable: function(){
        var self = this;
        BI.Popovers.close(BICst.LOGIN_INFO_POPOVER);
        var selectTablePane = BI.createWidget({
            type: "bi.select_one_table_pane",
            element: BI.Layers.create(BICst.SELECT_ONE_TABLE_LAYER, BICst.BODY_ELEMENT),
            translations: Data.SharingPool.get("translations")
        });
        BI.Layers.show(BICst.SELECT_ONE_TABLE_LAYER);
        selectTablePane.on(BI.SelectOneTablePane.EVENT_CHANGE, function (tables) {
            if (BI.isNotEmptyArray(tables)) {
                self.table = tables[0];
                self.populate();
                BI.Popovers.open(BICst.LOGIN_INFO_POPOVER);
            }
        });
    },

    populate: function(fieldName){
        var self = this;
        this.tableName.setValue(this.table.table_name);
        if(BI.isNotNull(this.table.fields)) {
            this._refreshByFields(this.table.fields, fieldName);
        }
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: this.element,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getTablesDetailInfoByTables([this.table], function(data){
            var fields = data[0].fields;
            self._refreshByFields(fields, fieldName);
            mask.destroy();
        });
    },

    _refreshByFields: function(fields, fieldName) {
        var items = [];
        BI.each(fields, function(i, fs){
            BI.each(fs, function(j, field){
                if(field.field_type === BICst.COLUMN.STRING) {
                    items.push({
                        type: "bi.text_button",
                        text: field.field_name,
                        value: field.field_name,
                        cls: "single-field",
                        hgap: 10,
                        height: 25
                    })
                }
            });
        });
        this.tablesGroup.populate(items);
        this.tablesGroup.setValue(fieldName);
        this.fieldName.setValue(fieldName);
        BI.isNotNull(this.clearButton) && this.clearButton.setVisible(BI.isNotNull(fieldName));
    },
    
    getValue: function(){
        return {
            field_name: this.fieldName.getValue(),
            table: this.table
        }
    }
});
BI.AuthorityLoginInfoPane.EVENT_CANCEL = "EVENT_CANCEL";
BI.AuthorityLoginInfoPane.EVENT_CLEAR = "EVENT_CLEAR";
BI.AuthorityLoginInfoPane.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.authority_login_info_pane", BI.AuthorityLoginInfoPane);