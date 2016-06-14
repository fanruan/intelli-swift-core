/**
 * Created by Young's on 2016/3/23.
 */
BI.IconMarkStyleSetting = BI.inherit(BI.Widget, {

    constants: {
        ICON_WIDTH: 16,
        ICON_HEIGHT: 16,
        ICON_CONTAINER_WIDTH: 26,
        ICON_CONTAINER_HEIGHT: 26,
        MARK_CONTAINER_WIDTH: 138,
        MARK_CONTAINER_HEIGHT: 30,
        LABEL_HEIGHT: 30,
        LABEL_WIDTH: 100
    },

    _defaultConfig: function(){
        return BI.extend(BI.IconMarkStyleSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-icon-mark-style-setting"
        })
    },

    _init: function(){
        BI.IconMarkStyleSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var mark = o.mark || 0;
        var level = o.num_level || "";
        var iconStyle = o.icon_style;
        BI.each(BICst.TARGET_STYLE_LEVEL, function(i, ob){
            if(ob.value === level){
                self.level = ob.text;
            }
        });

        this.leftMark = BI.createWidget({
            type: "bi.label",
            text: " ( -" + BI.i18nText("BI-Endless")+ ", " + mark + this.level + " ) ",
            title: " ( -" + BI.i18nText("BI-Endless")+ ", " + mark + this.level + " ) ",
            height: this.constants.LABEL_HEIGHT,
            width: this.constants.LABEL_WIDTH
        });
        this.leftIcon = BI.createWidget({
            type: "bi.icon_change_button",
            width: this.constants.ICON_CONTAINER_WIDTH,
            height: this.constants.ICON_CONTAINER_HEIGHT
        });
        this.centerMark = BI.createWidget({
            type: "bi.sign_editor",
            width: 90,
            errorText: BI.i18nText("BI-Numerical_Interval_Input_Data"),
            validationChecker: function(v){
                return BI.isNumeric(v);
            }
        });
        this.centerMark.setValue(mark);
        this.centerMark.on(BI.SignEditor.EVENT_CHANGE, function(){
            self.leftMark.setText("( -" + BI.i18nText("BI-Endless")+ ", " + this.getValue() + self.level + " ) ");
            self.rightMark.setText(" ( " + this.getValue() + self.level + ", +" + BI.i18nText("BI-Endless")+ " )");
            self.leftMark.setTitle("( -" + BI.i18nText("BI-Endless")+ ", " + this.getValue() + self.level + " ) ");
            self.rightMark.setTitle(" ( " + this.getValue() + self.level + ", +" + BI.i18nText("BI-Endless")+ " )");
        });
        this.centerUnit = BI.createWidget({
            type: "bi.label",
            text: this.level,
            height: this.constants.LABEL_HEIGHT
        });
        this.centerIcon = BI.createWidget({
            type: "bi.icon_change_button",
            width: this.constants.ICON_CONTAINER_WIDTH,
            height: this.constants.ICON_CONTAINER_HEIGHT
        });
        this.rightMark = BI.createWidget({
            type: "bi.label",
            text: " ( " + mark + this.level + ", +" + BI.i18nText("BI-Endless")+ " )",
            title: " ( " + mark + this.level + ", +" + BI.i18nText("BI-Endless")+ " )",
            height: this.constants.LABEL_HEIGHT,
            width: this.constants.LABEL_WIDTH
        });
        this.rightIcon = BI.createWidget({
            type: "bi.icon_change_button",
            width: this.constants.ICON_CONTAINER_WIDTH,
            height: this.constants.ICON_CONTAINER_HEIGHT
        });
        this.setIcon(iconStyle);

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [{
                type: "bi.label",
                text: "",
                width: this.constants.LABEL_WIDTH,
                height: this.constants.LABEL_HEIGHT
            }, {
                type: "bi.center_adapt",
                items: [this.leftMark, this.leftIcon],
                cls: "mark-container",
                width: this.constants.MARK_CONTAINER_WIDTH,
                height: this.constants.MARK_CONTAINER_HEIGHT
            }, {
                type: "bi.center_adapt",
                items: [this.centerMark, this.centerUnit, this.centerIcon],
                cls: "mark-container",
                width: this.constants.MARK_CONTAINER_WIDTH,
                height: this.constants.MARK_CONTAINER_HEIGHT
            }, {
                type: "bi.center_adapt",
                items: [this.rightMark, this.rightIcon],
                cls: "mark-container",
                width: this.constants.MARK_CONTAINER_WIDTH,
                height: this.constants.MARK_CONTAINER_HEIGHT
            }],
            hgap: 5
        })
    },

    setIcon: function(icon){
        var leftIcon = "target-style-less-dot-font",
            centerIcon = "target-style-equal-dot-font",
            rightIcon = "target-style-more-dot-font";
        if (icon === BICst.TARGET_STYLE.ICON_STYLE.ARROW){
            leftIcon = "target-style-less-arrow-font";
            centerIcon = "target-style-less-arrow-font";
            rightIcon = "target-style-less-arrow-font";
        }
        this.leftIcon.setIcon(leftIcon);
        this.rightIcon.setIcon(rightIcon);
        this.centerIcon.setIcon(centerIcon);
    },

    setLevel: function(level){
        var self = this;
        BI.each(BICst.TARGET_STYLE_LEVEL, function(i, ob){
            if(ob.value === level){
                self.level = ob.text;
            }
        });
        this.centerUnit.setText(this.level);
        this.leftMark.setText("( -" + BI.i18nText("BI-Endless")+ ", " + this.centerMark.getValue() + this.level + " ) ");
        this.rightMark.setText(" ( " + this.centerMark.getValue() + this.level + ", +" + BI.i18nText("BI-Endless")+ " )");
        this.leftMark.setTitle("( -" + BI.i18nText("BI-Endless")+ ", " + this.centerMark.getValue() + this.level + " ) ");
        this.rightMark.setTitle(" ( " + this.centerMark.getValue() + this.level + ", +" + BI.i18nText("BI-Endless")+ " )");
    },

    getValue: function(){
        return this.centerMark.getValue();
    }
});
$.shortcut("bi.icon_mark_style_setting", BI.IconMarkStyleSetting);