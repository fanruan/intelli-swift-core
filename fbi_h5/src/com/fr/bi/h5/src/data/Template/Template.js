import {each, some, find} from 'core'
import WidgetFactory from './Widget/WidgetFactory'
class Template {
    constructor(template) {
        this.$template = template;
    }

    $get() {
        return this.$template;
    }

    get$WidgetById(id) {
        return this.$template.getIn(['widgets', id]);
    }

    get$DimensionById(id) {
        return this.$template.getIn(['dimensions', id]);
    }

    getWidgetById(id) {
        return WidgetFactory.createWidget(this.get$WidgetById(id), id, this);
    }

    getDimensionById(id) {
        return DimensionFactory.createDimension(this.get$DimensionById(id), id, this.getWidgetById(this.getWidgetIDByDimensionID(id)));
    }

    getTargetById(id) {
        return DimensionFactory.createTarget(this.get$DimensionById(id), id, this.getWidgetById(this.getWidgetIDByDimensionID(id)));
    }

    getAllWidgetIds() {
        const res = [];
        this.$template.get('widgets').forEach(($widget, wId)=> {
            if (!WidgetFactory.createWidget($widget, wId, this).isControl()) {
                res.push(wId);
            }
        });
        return res;
    }

    getAllControlWidgetIds() {
        const res = [];
        this.$template.get('widgets').forEach(($widget, wId)=> {
            if (WidgetFactory.createWidget($widget, wId, this).isControl()) {
                res.push(wId);
            }
        });
        return res;
    }

    hasControlWidget() {
        return this.$template.get('widgets').some(($widget, wId)=> {
            return WidgetFactory.createWidget($widget, wId, this).isControl();
        });
    }

    isControlWidgetByWidgetId(wid) {
        var widgetType = this.getWidgetById(wid).getType();
        return widgetType === BICst.WIDGET.STRING ||
            widgetType === BICst.WIDGET.NUMBER ||
            widgetType === BICst.WIDGET.SINGLE_SLIDER ||
            widgetType === BICst.WIDGET.INTERVAL_SLIDER ||
            widgetType === BICst.WIDGET.DATE ||
            widgetType === BICst.WIDGET.MONTH ||
            widgetType === BICst.WIDGET.QUARTER ||
            widgetType === BICst.WIDGET.TREE ||
            widgetType === BICst.WIDGET.LIST_LABEL ||
            widgetType === BICst.WIDGET.TREE_LABEL ||
            widgetType === BICst.WIDGET.YEAR ||
            widgetType === BICst.WIDGET.YMD ||
            widgetType === BICst.WIDGET.GENERAL_QUERY;
    }

    isQueryControlExist() {
        var isQueryExist = false;
        this.$template.get('widgets').some(($widget, wId)=>{
            if (WidgetFactory.createWidget($widget, wId, this).getType() === BICst.WIDGET.QUERY) {
                return isQueryExist = true;
            }
        });
        return isQueryExist;
    }

    getWidgetInitTimeByID(wid) {
        var widget = this.template.getWidgetById(wid);
        return widget.getWidgetInitTime() || new Date().getTime();
    }

    isWidgetExistByID(wid) {
        return this.getAllWidgetIDs().contains(wid);
    }

    getWidgetIDByDimensionID(dId){
        if (!this._dimension2WidgetMap) {
            this._dimension2WidgetMap = {};
        }
        if (BI.isNotNull(this._dimension2WidgetMap[dId])) {
            return this._dimension2WidgetMap[dId];
        }
        var widgets = this.getAllWidgetIds();
        var wid = find(widgets, function (wid) {
            var dims = this.getWidgetById(wid).getAllDimensionIds();
            return find(dims, function (id) {
                return dId == id;
            })
        });
        this._dimension2WidgetMap[dId] = wid;
        return wid;
    }

    getWSTransferFilterById(wid) {
        var ws = this.getWidgetById(wid).getWidgetSettings();
        return BI.isNil(ws.transfer_filter) ? ws.transfer_filter :
            BICst.DEFAULT_CHART_SETTING.transfer_filter;
    }

    getDimensionFilterValueByID(did) {
        var dimension = this.getDimensionById(did);
        if (!isNil(dimension)) {
            return dimension.getFilterValue() || {};
        }
        return {};
    }

    getWidgetLinkageByID(wid){
        var widget = this.getWidgetById(wid);
        return widget.getLinkageValues();
    }

    set$Widget(id, $widget) {
        this.$template = this.$template.setIn(['widgets', id], $widget);
        return this;
    }
}

export default Template;