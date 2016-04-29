/**
 * Created by Young's on 2016/4/6.
 */
BI.WidgetFilter = BI.inherit(BI.Widget, {

    _constants: {
        SHOW_FILTER: 1,
        SHOW_NONE_FILTER: 2
    },

    _defaultConfig: function(){
        return BI.extend(BI.WidgetFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-widget-filter"
        })
    },

    _init: function(){
        BI.WidgetFilter.superclass._init.apply(this, arguments);
        var self = this;
        this.model = new BI.WidgetFilterModel();
        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: function(v){
                switch (v) {
                    case self._constants.SHOW_FILTER:
                        self.filterPane = BI.createWidget({
                            type: "bi.filter_pane",
                            cls: "filter-pane"
                        });
                        self.drills = BI.createWidget({
                            type: "bi.drill_filter_item",
                            wId: self.options.wId
                        });
                        return {
                            type: "bi.vertical",
                            items: [self.filterPane, self.drills],
                            hgap: 10,
                            vgap: 10
                        };
                    break;
                    case self._constants.SHOW_NONE_FILTER:
                        return {
                            type: "bi.vertical",
                            items: [{
                                type: "bi.label",
                                cls: "none-filter-tip",
                                text: BI.i18nText("BI-None_Filter"),
                                height: 50
                            }]
                        }
                }
            }
        });
        this.populate();
    },

    populate: function(){
        var self = this, o = this.options;
        var wId = o.wId;
        var items = [];

        var allWidgetIds = BI.Utils.getAllWidgetIDs();
        //找到所有控件的过滤条件
        BI.each(allWidgetIds, function(i, cwid){
            if(BI.Utils.isControlWidgetByWidgetId(cwid)) {
                var text = self.model.getControlWidgetValueTextByID(cwid);
                if(BI.isNotNull(text)) {
                    items.push({
                        type: "bi.control_filter_item",
                        wId: cwid,
                        text: text,
                        id: BI.UUID()
                    });
                }
            }
        });

        //组件的联动条件
        var linkageFilters = BI.Utils.getLinkageValuesByID(wId);

        //表头上设置的指标过滤条件
        var targetFilter = BI.Utils.getWidgetFilterValueByID(wId);

        BI.each(linkageFilters, function(tId, linkFilter){
            items.push({
                type: "bi.linkage_filter_item",
                id: BI.UUID(),
                tId: tId,
                filter: linkFilter,
                onRemoveFilter: function(tId, dId){
                    //这个地方就处理好clicked
                    var clicked = BI.Utils.getClickedByID(wId);
                    var values = clicked[tId];
                    BI.some(values, function(i, value){
                        if(value.dId === dId) {
                            values.splice(i, 1);
                            return true;
                        }
                    });
                    BI.isEmptyArray(values) && delete clicked[tId];
                    self.fireEvent(BI.WidgetFilter.EVENT_REMOVE_FILTER, {clicked: clicked});
                }
            });
        });

        BI.each(targetFilter, function(tId, filter){
            items.push(self.model.parseTargetFilter(tId, filter));
        });

        if(BI.isEmptyArray(items) && this.model.isEmptyDrillById(wId)) {
            this.tab.setSelect(this._constants.SHOW_NONE_FILTER);
            return ;
        }
        this.tab.setSelect(this._constants.SHOW_FILTER);
        var filterValues = [];
        if(items.length > 1) {
            filterValues.push({
                value: BICst.FILTER_TYPE.AND,
                children: items
            });
        } else {
            filterValues = items;
        }
        this.filterPane.populate(filterValues);
        this.drills.populate();
    }
});
BI.WidgetFilter.EVENT_REMOVE_FILTER = "EVENT_REMOVE_FILTER";
$.shortcut("bi.widget_filter", BI.WidgetFilter);