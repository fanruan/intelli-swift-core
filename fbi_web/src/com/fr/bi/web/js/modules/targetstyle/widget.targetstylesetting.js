/**
 * Created by Young's on 2016/3/22.
 */
BI.TargetStyleSetting = BI.inherit(BI.BarPopoverSection, {

    constants: {
        LABEL_HEIGHT: 30,
        LABEL_WIDTH: 100,
        BUTTON_HEIGHT: 26,
        BUTTON_WIDTH: 60,
        EDITOR_WIDTH: 80,
        EDITOR_HEIGHT: 28,
        SHOW_MARK: 1,
        HIDE_MARK: 2,
        NORMAL: 1,
        ZERO2POINT: 2,
        ONE2POINT: 3,
        TWO2POINT: 4,
    },

    _defaultConfig: function(){
        return BI.extend(BI.TargetStyleSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-style-setting"
        })
    },

    _init: function(){
        BI.TargetStyleSetting.superclass._init.apply(this, arguments);
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
                numLevel: BICst.TARGET_STYLE.NUM_LEVEL.NORMAL,
                unit: "",
                iconStyle: BICst.TARGET_STYLE.ICON_STYLE.NONE,
                mark: 0,
                conditions: []
            }
        }

        this.format = BI.createWidget({
            type: "bi.segment",
            items: BICst.TARGET_STYLE_FORMAT,
            width: 220
        });
        this.format.setValue(styleSettings.format);

        this.format.on(BI.Segment.EVENT_CHANGE, function () {
            example.setText(self._switchLabel());
            example.setTitle(self._switchLabel());
        });

        this.separators = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.separators.setSelected(styleSettings.numSeparators);

        this.separators.on(BI.Controller.EVENT_CHANGE, function () {
            example.setText(self._switchLabel());
            example.setTitle(self._switchLabel());
        });

        this.numLevel = BI.createWidget({
            type: "bi.segment",
            items: BICst.TARGET_STYLE_LEVEL,
            width: 275
        });
        this.numLevel.setValue(styleSettings.numLevel);
        this.numLevel.on(BI.Segment.EVENT_CHANGE, function(){
            BI.isNotNull(self.mark) && self.mark.setLevel(this.getValue()[0]);
            self.conditions.setNumLevel(this.getValue()[0]);
            example.setText(self._switchLabel());
            example.setTitle(self._switchLabel());
        });

        this.unit = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constants.EDITOR_WIDTH,
            height: this.constants.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.unit.setValue(styleSettings.unit);

        this.unit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            example.setText(self._switchLabel());
            example.setTitle(self._switchLabel());
        });

        this.iconStyle = BI.createWidget({
            type: "bi.icon_mark_combo",
            iconStyle: styleSettings.iconStyle,
            height: this.constants.EDITOR_HEIGHT,
            cls: "icon-mark-combo",
            width: 275
        });
        this.iconStyle.on(BI.IconMarkCombo.EVENT_CHANGE, function(){
            var v = this.getValue()[0];
            if( v === BICst.TARGET_STYLE.ICON_STYLE.ARROW ||
                v === BICst.TARGET_STYLE.ICON_STYLE.POINT ) {
                markTab.setSelect(self.constants.SHOW_MARK);
            } else {
                markTab.setSelect(self.constants.HIDE_MARK);
            }
            self.mark.setIcon(v);
        });
        var markTab = BI.createWidget({
            type: "bi.tab",
            direction: "custom",
            cardCreator: function(v){
                switch (v) {
                    case self.constants.HIDE_MARK:
                        return {
                            type: "bi.label",
                            height: 30
                        };
                    case self.constants.SHOW_MARK:
                        return self.mark = BI.createWidget({
                            type: "bi.icon_mark_style_setting",
                            mark: styleSettings.mark,
                            numLevel: self.numLevel.getValue()[0]
                        });
                }
            },
            height: this.constants.LABEL_HEIGHT
        });
        markTab.setSelect(styleSettings.iconStyle === BICst.TARGET_STYLE.ICON_STYLE.NONE ?
                            this.constants.HIDE_MARK : this.constants.SHOW_MARK);

        this.conditions = BI.createWidget({
            type: "bi.target_condition_style_setting",
            conditions: styleSettings.conditions,
            numLevel: styleSettings.numLevel
        });

        var example = BI.createWidget({
            type: "bi.label",
            height: 25,
            width: 110
        });

        example.setText(this._switchLabel());
        example.setTitle(this._switchLabel());

        BI.createWidget({
            type: "bi.vertical",
            element: center,
            cls: "bi-target-style-setting",
            items: [{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    cls: "style-name",
                    height: this.constants.LABEL_HEIGHT,
                    width: this.constants.LABEL_WIDTH,
                    textAlign: "left"
                }, this.numLevel, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    height: this.constants.LABEL_HEIGHT,
                    width: 50,
                    cls: "style-name"
                }, this.unit],
                hgap: 5
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "style-name",
                    height: this.constants.LABEL_HEIGHT,
                    width: this.constants.LABEL_WIDTH,
                    textAlign: "left"
                }, this.format, this.separators, example],
                hgap: 5
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Icon_Style"),
                    cls: "style-name",
                    height: this.constants.LABEL_HEIGHT,
                    width: this.constants.LABEL_WIDTH,
                    textAlign: "left"
                }, this.iconStyle],
                hgap: 5
            }, markTab, this.conditions],
            hgap: 10,
            vgap: 10
        })
    },

    _switchLabel: function() {
        return BI.TargetStyleSetting.formatNumber(this.numLevel.getValue()[0], this.format.getValue()[0],
        this.separators.isSelected(), this.unit.getValue());
    },

    end: function(){
        this.fireEvent(BI.TargetStyleSetting.EVENT_CHANGE);
    },

    getValue: function(){
        return {
            format: this.format.getValue()[0],
            numLevel: this.numLevel.getValue()[0],
            unit: this.unit.getValue(),
            iconStyle: this.iconStyle.getValue()[0],
            mark: BI.isNotNull(this.mark) ? this.mark.getValue() : 0,
            conditions: this.conditions.getValue(),
            numSeparators: this.separators.isSelected()
        }
    }
});
BI.TargetStyleSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.target_style_setting", BI.TargetStyleSetting);
BI.extend(BI.TargetStyleSetting, {
    formatNumberLevelAndSeparators: function (type, separators) {
        var formatter;
        switch (type) {
            case BICst.TARGET_STYLE.FORMAT.NORMAL:
                formatter = '#.##';
                if (separators) {formatter = '#,###.##'}
                break;
            case BICst.TARGET_STYLE.FORMAT.ZERO2POINT:
                formatter = '#0';
                if (separators) {formatter = '#,###'}
                break;
            case BICst.TARGET_STYLE.FORMAT.ONE2POINT:
                formatter = '#0.0';
                if (separators) {formatter = '#,###.0'}
                break;
            case BICst.TARGET_STYLE.FORMAT.TWO2POINT:
                formatter = '#0.00';
                if (separators) {formatter = '#,###.00'}
                break;
        }
        return formatter
    },

    getUnit: function (numberLevelType, axisUnit) {
        var unit = "";
        switch (numberLevelType) {
            case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                unit = "";
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                unit = BI.i18nText("BI-Wan");
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                unit = BI.i18nText("BI-Million");
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                unit = BI.i18nText("BI-Yi");
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                unit += '%';
                break;
        }
        return (BI.isEmptyString(unit) && BI.isEmptyString(axisUnit)) ? unit : (unit + axisUnit);
    },

    calcMagnify: function (numberLevel) {
        var magnify = 1;
        switch (numberLevel) {
            case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
            case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                magnify = 1;
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                magnify = 10000;
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                magnify = 1000000;
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                magnify = 100000000;
                break;
        }
        return magnify;
    },

    formatNumber: function (numberLevel, type, separators, axisUnit) {
        var num = 20000000;
        var format = this.formatNumberLevelAndSeparators(type, separators);
        format += this.getUnit(numberLevel, axisUnit);
        format += ';-' + format;
        return BI.i18nText('BI-Example') + '：' + BI.contentFormat(num.div(this.calcMagnify(numberLevel)), format )
    },
});