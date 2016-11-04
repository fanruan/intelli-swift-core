import {each, some, find, isNil} from 'core'
import WidgetFactory from './Widget/WidgetFactory'
import DimensionFactory from './Widget/Dimensions/DimensionFactory'
class Template {
    constructor(template) {
        this.$template = template;
    }

    forceUpdateAllWidgets(exceptWidgetIds = []) {
        const wIds = this.getAllWidgetIds();
        each(wIds, (wId)=> {
            if (exceptWidgetIds.indexOf(wId) > -1) {
                return;
            }
            const widget = this.getWidgetByWidgetId(wId);
            widget.forceUpdate();
            this.set$Widget(wId, widget.$get());
        });
    }

    //get

    $get() {
        return this.$template;
    }

    getAllWidgetIds() {
        const res = [];
        this.$template.get('widgets').forEach(($widget, wId)=> {
            res.push(wId);
        });
        return res;
    }

    getAllStatisticWidgetIds() {
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

    getWidgetByWidgetId(id) {
        return WidgetFactory.createWidget(this.get$WidgetByWidgetId(id), id, this);
    }

    getDimensionByDimensionId(dId) {
        return DimensionFactory.createDimension(this.get$DimensionByDimensionId(dId), dId, this.getWidgetByWidgetId(this.getWidgetIdByDimensionId(dId)));
    }

    getAllDimensionIds() {
        let ids = [];
        const allWIds = this.getAllWidgetIds();
        allWIds.forEach((wid)=> {
            ids = ids.concat(this.getWidgetByWidgetId(wid).getAllDimensionIds());
        });
        return ids;
    }

    get$WidgetByWidgetId(id) {
        return this.$template.getIn(['widgets', id]);
    }

    get$DimensionByDimensionId(id) {
        let $dimension = null;
        some(this.getAllWidgetIds(), (wId)=> {
            const widget = this.getWidgetByWidgetId(wId);
            if (widget.hasDimensionByDimensionId(id)) {
                $dimension = widget.get$DimensionByDimensionId(id);
                return true;
            }
        });
        return $dimension;
    }

    getWidgetInitTimeByWidgetId(wId) {
        var widget = this.template.getWidgetByWidgetId(wId);
        return widget.getWidgetInitTime() || new Date().getTime();
    }

    getWidgetIdByDimensionId(dId) {
        if (!this._dimension2WidgetMap) {
            this._dimension2WidgetMap = {};
        }
        if (!isNil(this._dimension2WidgetMap[dId])) {
            return this._dimension2WidgetMap[dId];
        }
        var widgets = this.getAllWidgetIds();
        var wid = find(widgets, (wid)=> {
            var dims = this.getWidgetByWidgetId(wid).getAllDimensionIds();
            return find(dims, (id)=> {
                return dId == id;
            })
        });
        this._dimension2WidgetMap[dId] = wid;
        return wid;
    }

    getWSTransferFilterByWidgetId(wid) {
        var ws = this.getWidgetByWidgetId(wid).getWidgetSettings();
        return isNil(ws.transfer_filter) ? ws.transfer_filter :
            BICst.DEFAULT_CHART_SETTING.transfer_filter;
    }

    getDimensionFilterValueByDimensionId(did) {
        var dimension = this.getDimensionByDimensionId(did);
        return isNil(dimension) ? {} : dimension.getFilterValue();
    }

    getWidgetLinkageByWidgetId(wid) {
        const widget = this.getWidgetByWidgetId(wid);
        return widget.getWidgetLinkage();
    }

    getWidgetLinkageValueByWidgetId(wid) {
        var widget = this.getWidgetByWidgetId(wid);
        return widget.getLinkageValues();
    }

    getFieldIdByDimensionId(dId) {
        var dimension = this.getDimensionByDimensionId(dId);
        if (!isNil(dimension)) {
            return dimension.getFieldId();
        }
    }

    //has

    hasControlWidget() {
        return this.$template.get('widgets').some(($widget, wId)=> {
            return WidgetFactory.createWidget($widget, wId, this).isControl();
        });
    }

    hasQueryControlWidget() {
        var isQueryExist = false;
        this.$template.get('widgets').some(($widget, wId)=> {
            const control = WidgetFactory.createWidget($widget, wId, this);
            if (control.isControl() && control.isQueryControl()) {
                return isQueryExist = true;
            }
        });
        return isQueryExist;
    }

    hasWidgetByWidgetId(wId) {
        return this.getAllWidgetIds().indexOf(wId) > -1;
    }

    hasDimensionByDimensionId(dId) {
        return !isNil(this.getWidgetIdByDimensionId(dId));
    }

    //is

    isControlWidgetByWidgetId(wId) {
        return this.getWidgetByWidgetId(wId).isControl();
    }

    isDimDimensionByDimensionId(dId) {
        if (this.hasDimensionByDimensionId(dId)) {
            const wId = this.getWidgetIdByDimensionId(dId);
            return this.getWidgetByWidgetId(wId).isDimDimensionByDimensionId(dId);
        }
        return false;
    }

    //set

    set$Widget(id, $widget) {
        this.$template = this.$template.setIn(['widgets', id], $widget);
        return this;
    }
}

export default Template;