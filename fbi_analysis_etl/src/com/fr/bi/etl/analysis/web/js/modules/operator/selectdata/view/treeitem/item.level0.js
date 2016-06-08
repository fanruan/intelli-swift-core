/**
 * Created by GUY on 2015/9/6.
 * @class BI.AnalysisETLDetailSelectDataLevel0Item
 * @extends BI.Single
 */
BI.AnalysisETLDetailSelectDataLevel0Item = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLDetailSelectDataLevel0Item.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-data-level0-item",
            height: 25,
            hgap: 0,
            fieldType: BICst.COLUMN.STRING,
            lgap: 0,
            rgap: 35
        })
    },

    _getFieldClass: function (type) {
        switch (type) {
            case BICst.COLUMN.STRING:
                return "select-data-field-string-font";
            case BICst.COLUMN.NUMBER:
                return "select-data-field-number-font";
            case BICst.COLUMN.DATE: {
                return "select-data-field-date-font";
            }
            case BICst.COLUMN.COUNTER:
                return "select-data-field-number-font";
        }
    },

    _createNewType : function (type, group) {
        if(type ===  BICst.COLUMN.DATE)  {
            type = BI.Utils.createDateFieldType(group["type"])
        }
        return type;
    },

    _init: function () {
        BI.AnalysisETLDetailSelectDataLevel0Item.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.button = BI.createWidget({
            type: "bi.blank_icon_text_item",
            // trigger: "mousedown",
            cls: "select-data-level0-item-button " + this._getFieldClass(this._createNewType(o.fieldType, o.value["group"])),
            forceNotSelected:true,
            blankWidth: 10,
            text: o.text,
            value: o.value,
            height: 25,
            textLgap: 10,
            textRgap: 5
        });
        this.button.on(BI.Controller.EVENT_CHANGE, function (type) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.CLICK, self.getValue(), self);
        });
        this.previewBtn = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Preview"),
            title: BI.i18nText("BI-Preview")
        });
        this.previewBtn.doHighLight();
        this.previewBtn.on(BI.TextButton.EVENT_CHANGE, function(){
            BI.Popovers.create(self.getName(), BI.createWidget({
                type: "bi.detail_select_data_preview_section",
                text: o.text,
                value: o.value
            }), {container: BI.Layers.get(ETLCst.ANALYSIS_LAYER)}).open(self.getName());
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.button,
                top: 0,
                left: o.lgap,
                right: o.rgap
            }, {
                el: {
                    type: "bi.center_adapt",
                    items: [this.previewBtn]
                },
                top: 0,
                right: 0,
                bottom: 0,
                width: o.rgap
            }]
        });
        this.previewBtn.invisible();
        this.element.hover(function(){
            if(BI.Utils.getFieldTypeByID(o.value) === BICst.COLUMN.COUNTER){
                return;
            }
            self.previewBtn.visible();
        }, function(){
            self.previewBtn.invisible();
        });
        //标蓝
        BI.Utils.isSrcUsedBySrcID(o.id) === true && this.doHighLight();
        BI.Broadcasts.on(BICst.BROADCAST.SRC_PREFIX + o.id, function(v){
            if(v === true){
                self.doHighLight();
            } else {
                if(BI.Utils.isSrcUsedBySrcID(o.id) === false){
                    self.unHighLight();
                }
            }
        });
        if(BI.isFunction(o.listener)) {
            o.listener.apply(this);
        }
    },

    setEnable : function (v) {
        BI.AnalysisETLDetailSelectDataLevel0Item.superclass.setEnable.apply(this, arguments)
        this.button.setEnable(v);
        this.previewBtn.setEnable(v)
    },

    isSelected: function () {
        return this.button.isSelected();
    },

    setSelected: function (b) {
        this.button.setSelected(b);
    },

    setTopLineVisible: function () {
    },

    setTopLineInVisible: function () {
    },

    setBottomLineVisible: function () {
    },

    setBottomLineInVisible: function () {
    },

    doRedMark: function () {
        this.button.doRedMark.apply(this.button, arguments);
    },

    unRedMark: function () {
        this.button.unRedMark.apply(this.button, arguments);
    },

    doHighLight: function () {
        this.button.doHighLight.apply(this.button, arguments);
    },

    unHighLight: function () {
        this.button.unHighLight.apply(this.button, arguments);
    }
});

$.shortcut("bi.analysis_etl_detail_select_data_level0_item", BI.AnalysisETLDetailSelectDataLevel0Item);