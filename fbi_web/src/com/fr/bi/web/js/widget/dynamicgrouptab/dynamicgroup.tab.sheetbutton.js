/**
 * Created by windy on 2016/12/21.
 */
BI.DynamicGroupTabSheetButton = BI.inherit(BI.BasicButton, {

    _constant : {
        popupItemHeight:25,
        RENAME: 1,
        COPY: 2,
        DELETE: 3,
        ICON_WIDTH: 28,
        ICON_HEIGHT: 28
    },

    _defaultConfig: function () {
        return BI.extend(BI.DynamicGroupTabSheetButton.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-tab-sheet-button bi-list-item",
            height: 28,
            width:null,
            iconWidth: 13,
            iconHeight: 13,
            text : "sheet1",
            value : 1
        })
    },

    _init : function (){
        BI.DynamicGroupTabSheetButton.superclass._init.apply(this, arguments);
        var self = this, o = this.options, c = this._constant;
        this.noError = true;
        this.text = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            whiteSpace: "nowrap",
            textHeight: o.height,
            height: o.height,
            lgap: 10,
            text: o.text,
            title: o.text,
            value: o.value,
            py: o.py
        });
        this.combo = BI.createWidget({
            type: "bi.combo",
            isNeedAdjustWidth: false,
            el: {
                type: "bi.icon_trigger",
                extraCls: "icon-analysis-table-set",
                width: c.ICON_WIDTH,
                height: c.ICON_HEIGHT
            },
            popup: {
                el: {
                    type: "bi.button_group",
                    chooseType:BI.Selection.None,
                    items: BI.createItems(this._createItemList(), {
                        type: "bi.icon_text_item",
                        cls: "bi-list-item-hover",
                        height: c.popupItemHeight
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            }
        });
        this.combo.on(BI.Combo.EVENT_CHANGE, function (v) {
            switch (v) {
                case c.DELETE:{
                    BI.Msg.confirm(BI.i18nText("BI-Confirm_Delete"),  BI.i18nText("BI-Confirm_Delete") +  self.text.getValue() + "?", function (v) {
                        if(v === true) {
                            self.fireEvent(BI.DynamicGroupTabSheetButton.EVENT_DELETE, o.value);
                        }
                    })
                    break;
                }
                case c.COPY:{
                    self.fireEvent(BI.DynamicGroupTabSheetButton.EVENT_COPY, o.value);
                    break;
                }
                case c.RENAME:{
                    self.fireEvent(BI.DynamicGroupTabSheetButton.EVENT_RENAME, o.value);
                    break;
                }
            }

            self.fireEvent(BI.WidgetCombo.EVENT_CHANGE, v);
            this.hideView();
        });

        BI.createWidget({
            type:"bi.htape",
            element:this.element,
            items: [this.text, {
                el: {
                    type:"bi.vertical_adapt",
                    items:[this.combo]
                },
                width : c.ICON_WIDTH,
            }]
        })
    },

    _createItemList : function (){
        var c = this._constant;
        return [{
            text: BI.i18nText("BI-Rename"),
            value: c.RENAME,
            title: BI.i18nText("BI-Rename"),
            cls: "rename-font"
        }, {
            text: BI.i18nText("BI-Copy"),
            title: BI.i18nText("BI-Copy"),
            value: c.COPY,
            cls: "widget-copy-h-font"
        }, {
            text: BI.i18nText("BI-Remove"),
            title: BI.i18nText("BI-Remove"),
            value: c.DELETE,
            cls: "widget-delete-h-font"
        }];
    },

    _refreshRedMark : function () {
        this.noError === true ? this.text.unRedMark("") : this.text.doRedMark(this.options.text);
    },

    setValid : function (isValid) {
        this.noError = isValid
        this._refreshRedMark();
    },

    setText : function(text) {
        BI.DynamicGroupTabSheetButton.superclass.setText.apply(this, arguments);
        this.text.setText(text);
        this._refreshRedMark();
    },

    getValue: function(){
        return this.options.value;
    },

})

BI.DynamicGroupTabSheetButton.EVENT_COPY = "EVENT_COPY";
BI.DynamicGroupTabSheetButton.EVENT_RENAME = "EVENT_RENAME";
BI.DynamicGroupTabSheetButton.EVENT_DELETE = "EVENT_DELETE";
$.shortcut("bi.dynamic_group_tab_sheet_button", BI.DynamicGroupTabSheetButton);