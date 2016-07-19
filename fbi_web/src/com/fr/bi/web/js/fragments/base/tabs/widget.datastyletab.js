/**
 * @class BI.DataStyleTab
 * @extend BI.Widget
 * 数据/样式tab
 */
BI.DataStyleTab = BI.inherit(BI.Widget, {

    constants: {
        DETAIL_TAB_WIDTH: 200,
        DETAIL_TAB_HEIGHT: 40
    },

    _defaultConfig: function(){
        return BI.extend(BI.DataStyleTab.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-widget-attr-tab",
            wId: ""
        })
    },

    _init: function(){
        BI.DataStyleTab.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            tab: {
                items: BI.createItems(BICst.DETAIL_DATA_STYLE_TAB, {
                    type: "bi.line_segment_button",
                    height: this.constants.DETAIL_TAB_HEIGHT
                }),
                height: this.constants.DETAIL_TAB_HEIGHT,
                layouts: [{
                    type: "bi.absolute_center_adapt",
                    items: [{
                        type: "bi.center",
                        width: this.constants.DETAIL_TAB_WIDTH,
                        height: this.constants.DETAIL_TAB_HEIGHT
                    }]
                }]
            },
            cardCreator: o.cardCreator
        });
        this.tab.setSelect(BICst.DETAIL_TAB_TYPE_DATA);

        this.tab.on(BI.Tab.EVENT_CHANGE, function(){
           self.fireEvent(BI.DataStyleTab.EVENT_CHANGE);
        });
    },

    getSelect: function(){
        return this.tab.getSelect();
    }
});
BI.DataStyleTab.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.data_style_tab", BI.DataStyleTab);