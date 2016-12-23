/**
 * Created by zcf on 12/22/2016.
 */
BI.CustomMultiDatePane = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CustomMultiDatePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-multi-date-pane"
        })
    },

    _init: function (){
        BI.CustomMultiDatePane.superclass._init.apply(this, arguments);

        var self = this;
        this.datapane=BI.createWidget({
            type: "bi.date_calendar_popup",
            element:this.element
        });
        this.datapane.on(BI.DateCalendarPopup.EVENT_CHANGE,function () {
            self.fireEvent(BI.CustomMultiDatePane.EVENT_CHANGE);
        })
    },

    getValue:function () {
        return this.datapane.getValue();
    },

    setValue:function (v) {
        if(BI.isEmptyObject(v)){
            var date = new Date();
            this.datapane.setValue({
                year: date.getFullYear(),
                month: date.getMonth(),
                day: date.getDate()
            });
        }else {
            this.datapane.setValue(v);
        }
    }
});
BI.CustomMultiDatePane.EVENT_CHANGE="BI.CustomMultiDatePane.EVENT_CHANGE";
$.shortcut("bi.custom_multi_date_pane", BI.CustomMultiDatePane);