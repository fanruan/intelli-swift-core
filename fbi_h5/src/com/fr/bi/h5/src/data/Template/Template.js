import {each, some, find, isNil} from 'core'
import WidgetFactory from './Widget/WidgetFactory'
import DimensionFactory from './Widget/Dimensions/DimensionFactory'
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
        let $dimension;
        some(this.getAllWidgetIds().concat(this.getAllControlWidgetIds()), (wId)=> {
            const widget = this.getWidgetById(wId);
            if (widget.hasDimensionById(id)) {
                $dimension = widget.get$DimensionById(id);
                return true;
            }
        });
        return $dimension;
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

    getAllDimensionAndTargetIds() {
        let ids = [];
        const allWIds = this.getAllWidgetIds();
        allWIds.forEach((wid)=> {
            ids = ids.concat(this.getWidgetById(wid).getAllDimensionAndTargetIds());
        });
        return ids;
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
        return this.getWidgetById(wid).isControl();
    }

    isQueryControlExist() {
        var isQueryExist = false;
        this.$template.get('widgets').some(($widget, wId)=> {
            const control = WidgetFactory.createWidget($widget, wId, this);
            if (control.isControl() && control.isQueryControl()) {
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
        return this.getAllWidgetIds().indexOf(wid) > -1;
    }

    getWidgetIDByDimensionID(dId) {
        if (!this._dimension2WidgetMap) {
            this._dimension2WidgetMap = {};
        }
        if (!isNil(this._dimension2WidgetMap[dId])) {
            return this._dimension2WidgetMap[dId];
        }
        var widgets = this.getAllWidgetIds().concat(this.getAllControlWidgetIds());
        var wid = find(widgets, (wid)=> {
            var dims = this.getWidgetById(wid).getAllDimensionAndTargetIds();
            return find(dims, (id)=> {
                return dId == id;
            })
        });
        this._dimension2WidgetMap[dId] = wid;
        return wid;
    }

    getWSTransferFilterById(wid) {
        var ws = this.getWidgetById(wid).getWidgetSettings();
        return isNil(ws.transfer_filter) ? ws.transfer_filter :
            BICst.DEFAULT_CHART_SETTING.transfer_filter;
    }

    getDimensionFilterValueByID(did) {
        var dimension = this.getDimensionById(did);
        return isNil(dimension) ? {} : dimension.getFilterValue();
    }

    getWidgetLinkageByID(wid) {
        const widget = this.getWidgetById(wid);
        return widget.getWidgetLinkage();
    }

    getWidgetLinkageValueByID(wid) {
        var widget = this.getWidgetById(wid);
        return widget.getLinkageValues();
    }

    getFieldIDByDimensionID(did) {
        var dimension = this.getDimensionById(did);
        if (!isNil(dimension)) {
            return dimension.getFieldId();
        }
    }

    set$Widget(id, $widget) {
        this.$template = this.$template.setIn(['widgets', id], $widget);
        return this;
    }
}

export default Template;