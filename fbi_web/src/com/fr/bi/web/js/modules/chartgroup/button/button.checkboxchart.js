/**
 * 组件详细设置的图表类型选择
 * create by young
 * 2015/6/30
 * @type {*|void|Object}
 */
BI.CheckboxChartButton = BI.inherit(BI.Single, {

    constants: {
        BUTTON_HEIGHT: 34,
        BUTTON_WIDTH: 50
    },

    _defaultConfig: function(){
        return BI.extend(BI.CheckboxChartButton.superclass._defaultConfig.apply(this, arguments),{
            iconCls: ""
        })
    },

    _init:function() {
        BI.CheckboxChartButton.superclass._init.apply(this, arguments);
        var self = this;
        this.checkbox = BI.createWidget({
            type: "bi.checkbox",
            cls: "chart-type-checkbox",
            stopPropagation: true,
            handler: function(){
                self.checkbox.setSelected(self.isSelected() || self.checkbox.isSelected());
            }
        });
        this.icon = BI.createWidget({
            type: "bi.icon_button",
            cls: this.options.iconCls
        });
        this.icon.on(BI.IconButton.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.CLICK);
        });
        BI.createWidget({
            element: this.element,
            type: "bi.absolute",
            cls: "detail-chart-type",
            items: [{
                el: BI.createWidget({
                    type: "bi.center_adapt",
                    items: [this.icon]
                }),
                left: 0,
                top: 0,
                bottom: 0,
                right: 0
            }, {
                el: this.checkbox,
                left: 0,
                top: 10
            }],
            height: this.constants.BUTTON_HEIGHT,
            width: this.constants.BUTTON_WIDTH
        });
    },

    setSelected: function(v){
        this.options.selected = v;
        this.icon.setSelected(v);
        if(v === true){
            this.checkbox.setSelected(v);
        }
    },

    isCheckboxSelected: function(){
        return this.checkbox.isSelected();
    },

    isSelected: function(){
        return this.icon.isSelected();
    }
});
$.shortcut("bi.checkbox_chart_button", BI.CheckboxChartButton);