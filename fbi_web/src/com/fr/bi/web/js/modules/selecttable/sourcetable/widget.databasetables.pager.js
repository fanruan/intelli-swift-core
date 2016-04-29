/**
 * @class BI.DatabaseTablesPager
 * @extend BI.Widget
 * 表页码
 */
BI.DatabaseTablesPager = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.DatabaseTablesPager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "table-pager"
        })
    },

    _init: function(){
        BI.DatabaseTablesPager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.pager = BI.createWidget({
            type: "bi.pager",
            element: this.element,
            dynamicShow: false,
            dynamicShowFirstLast: true,
            height: 50,
            pages: o.pages,
            groups: 5,
            curr: 1,
            first: 1,
            last: o.pages,
            prev: {
                type: "bi.icon_button",
                cls: "pre-page-font previous-next-page",
                title: BI.i18nText("BI-Previous_Page"),
                value: "prev",
                once: false,
                height: 23,
                width: 23
            },
            next: {
                type: "bi.icon_button",
                cls: "next-page-font previous-next-page",
                title: BI.i18nText('BI-Next_Page'),
                value: "next",
                once: false,
                height: 23,
                width: 23
            },
            layouts: [{
                type: "bi.horizontal",
                hgap: 3,
                vgap: 10
            }]
        });
        this.pager.on(BI.Controller.EVENT_CHANGE, function(){
            arguments[1] = self.pager.getCurrentPage();
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
    },

    getValue: function(){
        return this.pager.getValue();
    },

    setValue: function(v){
        this.pager.setValue(v);
    },

    getCurrentPage: function(){
        return this.pager.getCurrentPage();
    }
});
$.shortcut("bi.database_tables_pager", BI.DatabaseTablesPager);