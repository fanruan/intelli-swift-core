/**
 * Created by Young's on 2016/4/22.
 */
BI.TimeSettingItem = BI.inherit(BI.Widget, {

    _defaultConfig: function(){
        return BI.extend(BI.TimeSettingItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-time-setting-item",
            frequency: BICst.UPDATE_FREQUENCY.EVER_DAY,
            time: 0
        })
    },

    _init: function(){
        BI.TimeSettingItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.id = BI.UUID();
        this.frequency = BI.createWidget({
            type: "bi.text_icon_check_combo",
            items: [{
                text: BI.i18nText("BI-Ev_Month"),
                value: BICst.UPDATE_FREQUENCY.EVER_MONTH
            }, {
                text: BI.i18nText("BI-Ev_Day"),
                value: BICst.UPDATE_FREQUENCY.EVER_DAY
            }, {
                text: BI.i18nText("BI-Ev_Monday"),
                value: BICst.UPDATE_FREQUENCY.EVER_MONDAY
            }, {
                text: BI.i18nText("BI-Ev_Tuesday"),
                value: BICst.UPDATE_FREQUENCY.EVER_TUESDAY
            }, {
                text: BI.i18nText("BI-Ev_Wednesday"),
                value: BICst.UPDATE_FREQUENCY.EVER_WEDNESDAY
            }, {
                text: BI.i18nText("BI-Ev_Thursday"),
                value: BICst.UPDATE_FREQUENCY.EVER_THURSDAY
            }, {
                text: BI.i18nText("BI-Ev_Friday"),
                value: BICst.UPDATE_FREQUENCY.EVER_FRIDAY
            }, {
                text: BI.i18nText("BI-Ev_Saturday"),
                value: BICst.UPDATE_FREQUENCY.EVER_SATURDAY
            }, {
                text: BI.i18nText("BI-Ev_Sunday"),
                value: BICst.UPDATE_FREQUENCY.EVER_SUNDAY
            }],
            width: 160,
            height: 28
        });
        this.frequency.on(BI.TextIconCheckCombo.EVENT_CHANGE, function(v){
            var v = this.getValue()[0];
            self.day.setVisible(v === BICst.UPDATE_FREQUENCY.EVER_MONTH);
            self.hour.setVisible(v !== BICst.UPDATE_FREQUENCY.EVER_MONTH);
            self.options.onSettingChange();
        });
        this.frequency.setValue(o.frequency);

        this.day = BI.createWidget({
            type: "bi.day_time_setting"
        });
        this.day.on(BI.DayTimeSetting.EVENT_CHANGE, function(){
            self.options.onSettingChange();
        });
        this.hour = BI.createWidget({
            type: "bi.hour_time_setting"
        });
        this.hour.on(BI.HourTimeSetting.EVENT_CHANGE, function(){
            self.options.onSettingChange();
        });
        if(o.frequency === BICst.UPDATE_FREQUENCY.EVER_MONTH) {
            this.day.setVisible(true);
            this.hour.setVisible(false);
            this.day.setValue(o.time);
        } else {
            this.day.setVisible(false);
            this.hour.setVisible(true);
            this.hour.setValue(o.time);
        }

        var remove = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-red-font",
            width: 25,
            height: 25
        });
        remove.on(BI.IconButton.EVENT_CHANGE, function(){
            self.options.onRemoveSetting(self.id);
        });

        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            element: this.element,
            items: {
                left: [this.frequency, this.hour, this.day, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Process_Update"),
                    height: 30
                }],
                right: [remove]
            },
            llgap: 10,
            rrgap: 10,
            height: 40
        })
    },

    isSelected: function(){
        return true;
    },

    setSelected: function(){

    },

    getValue: function(){
        return {
            frequency: this.frequency.getValue()[0],
            time: this.day.isVisible() ? this.day.getValue() : this.hour.getValue(),
            id: this.id
        }
    },

    setValue: function(v){
        this.frequency.setValue(v.frequency);
        this.time.setValue(v.time);
    }
});
$.shortcut("bi.time_setting_item", BI.TimeSettingItem);