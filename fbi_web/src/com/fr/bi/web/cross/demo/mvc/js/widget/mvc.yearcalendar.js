YearCalendarView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(YearCalendarView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-year-calendar bi-mvc-layout"
        })
    },

    _init: function(){
        YearCalendarView.superclass._init.apply(this, arguments);
    },

    _createNav: function(v){
        var y = this.YEAR;

        var calendar = BI.createWidget({
            type: "bi.year_calendar",
            min: '2000-01-01',
            max: '2019-12-31',
            logic: {
                dynamic: false
            },
            year: y + v*12
        })
        calendar.setValue(this.YEAR);
        return calendar;
    },

    _render: function(vessel){
        var self = this;
        this.YEAR = new Date().getFullYear();
        var selectedTime = this.YEAR;

        var tip = BI.createWidget({
            type: "bi.label"
        });

        var backBtn = BI.createWidget({
            type: "bi.button",
            once: false,
            text: "后退",
            value: -1
        });

        var preBtn = BI.createWidget({
            type: "bi.button",
            once: false,
            text: "前进",
            value: 1
        })

        var nav = BI.createWidget({
            type: "bi.navigation",
            element: vessel,
            tab: {
                height: 30,
                items: [backBtn, tip, preBtn],
                layouts: [{
                    type: "bi.center"
                }]
            },
            cardCreator: BI.bind(this._createNav, this),

            afterCardCreated: function(){

            },

            afterCardShow: function(){
                this.setValue(selectedTime);
                var calendar = this.getSelectedCard();
                backBtn.setEnable(!calendar.isFrontYear());
                preBtn.setEnable(!calendar.isFinalYear());
            }
        })

        nav.on(BI.Navigation.EVENT_CHANGE, function(v){
            selectedTime = v;
            tip.setText(v);
        });

        tip.setText(this.YEAR);
    }
});
YearCalendarModel = BI.inherit(BI.Model, {

});