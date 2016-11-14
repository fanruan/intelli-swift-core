BI.TargetStyleSettingForMap = BI.inherit(BI.BarPopoverSection, {

    constants: {
        LABEL_HEIGHT: 30,
        LABEL_WIDTH: 100,
        BUTTON_HEIGHT: 26,
        BUTTON_WIDTH: 60,
        EDITOR_WIDTH: 80,
        EDITOR_HEIGHT: 28,
        SHOW_MARK: 1,
        HIDE_MARK: 2
    },

    _defaultConfig: function(){
        return BI.extend(BI.TargetStyleSettingForMap.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-style-setting"
        })
    },

    _init: function(){
        BI.TargetStyleSettingForMap.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function(north){
        var name = BI.Utils.getDimensionNameByID(this.options.dId);
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: name + BI.i18nText("BI-Target_Style_Show"),
            height: 50,
            textAlign: "left"
        })
    },

    rebuildCenter: function(center){
        var self = this, o = this.options;
        var dId = o.dId;
        var styleSettings = BI.Utils.getDimensionSettingsByID(dId);
        if(BI.isEmptyObject(styleSettings)) {
            styleSettings = {
                format: BICst.TARGET_STYLE.FORMAT.NORMAL,
                num_level: BICst.TARGET_STYLE.NUM_LEVEL.NORMAL
            }
        }

        this.format = BI.createWidget({
            type: "bi.segment",
            items: BICst.TARGET_STYLE_FORMAT,
            width: 184
        });
        this.format.setValue(styleSettings.format);

        this.separators = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        var example = BI.createWidget({
            type: "bi.label",
            height: 25
        });

        this.showSeparators = styleSettings.num_separators || false;

        this.separators.setSelected(this.showSeparators);

        example.setText(this._switchLabel(this.showSeparators));

        this.separators.on(BI.Controller.EVENT_CHANGE, function () {
            self.showSeparators = !self.showSeparators;
            example.setText(self._switchLabel(self.showSeparators));
        });

        this.numLevel = BI.createWidget({
            type: "bi.segment",
            items: BICst.TARGET_STYLE_LEVEL_SHORT,
            width: 230
        });
        this.numLevel.setValue(styleSettings.num_level);

        this.unit = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constants.EDITOR_WIDTH,
            height: this.constants.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.unit.setValue(styleSettings.unit);

        BI.createWidget({
            type: "bi.left",
            element: center,
            cls: "bi-target-style-setting",
            items: [{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "style-name",
                    height: this.constants.LABEL_HEIGHT,
                    textAlign: "left"
                }, this.format, this.separators, example],
                hgap: 5
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    cls: "style-name",
                    height: this.constants.LABEL_HEIGHT,
                    textAlign: "left"
                }, this.numLevel, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    height: this.constants.LABEL_HEIGHT,
                    width: 50,
                    cls: "style-name"
                }, this.unit],
                hgap: 5
            }],
            hgap: 10,
            vgap: 10
        })
    },

    _switchLabel: function(v) {
        return v ? BI.i18nText("BI-Separators_Example") : BI.i18nText("BI-None_Separator_Example")
    },

    end: function(){
        this.fireEvent(BI.TargetStyleSettingForMap.EVENT_CHANGE);
    },

    getValue: function(){
        return {
            format: this.format.getValue()[0],
            numLevel: this.numLevel.getValue()[0],
            numSeparators: this.separators.isSelected(),
            unit: this.unit.getValue()
        }
    }
});
BI.TargetStyleSettingForMap.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.target_style_setting_for_map", BI.TargetStyleSettingForMap);