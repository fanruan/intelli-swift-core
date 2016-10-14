import {each, first, arrayMove, values, keys} from 'core'
import {Widget, Template, Dimension} from 'data'
export default class SettingsComponentHelper {
    constructor(props, context) {
        this.widget = new Widget(props.$widget, props.wId, new Template(context.$template));

    }

    getViewItems() {
        const result = [];
        each(this._getViewIds(), (viewId)=> {
            result.push({
                text: '行表头',
                viewId: viewId,
            })
        });
        return result;
    }

    getDimensionsItems(viewId) {
        const result = [];
        each(this._getDimensionIds(viewId), (dId)=> {
            const dim = this.widget.getDimensionOrTargetById(dId);
            result.push({
                text: dim.getName(),
                viewId: viewId,
                dId: dId
            })
        });
        return result;
    }

    _getViewIds() {
        const view = this.widget.getWidgetView();
        return keys(view);
    }

    _getDimensionIds(viewId) {
        const view = this.widget.getWidgetView();
        return view[viewId] || [];
    }

    doMove(viewId, oldIndex, newIndex) {
        const view = this.widget.getWidgetView();
        const items = this._getDimensionIds(viewId);
        arrayMove(items, oldIndex, newIndex);
        view[viewId] = items;
        this.widget.setWidgetView(view);
        return this.widget.$get();
    }

    set$Dimension($dimension, dId) {
        return this.widget.set$Dimension($dimension, dId).$get();
    }
}