import {each, first, arrayMove, values, keys, isNil} from 'core'
import {WidgetFactory, TemplateFactory} from 'data'
export default class SettingsComponentHelper {
    constructor(props, context) {
        this.widget = WidgetFactory.createWidget(props.$widget, props.wId, TemplateFactory.createTemplate(context.$template));

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

    getViewItemByViewId(viewId) {
        return {
            text: '行表头',
            viewId: viewId
        }
    }

    isDimensionByDimensionId(dId) {
        return this.widget.isDimensionById(dId);
    }

    getAllDimensionItems() {
        const result = {};
        each(this._getViewIds(), (viewId)=> {
            result[viewId] = this.getDimensionsItems(viewId);
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

    _getPreViewIdByViewId(viewId) {
        const view = this.widget.getWidgetView();
        const vs = keys(view);
        let idx = -1;
        if ((idx = vs.indexOf(viewId)) > 0) {
            return vs[idx - 1];
        }
    }

    doMove(oldIndex, newIndex) {
        const oldValue = oldIndex.split('-');
        const [oViewId,oIndex] = oldValue;
        const newValue = newIndex.split('-');
        const [nViewId,nIndex] = newValue;
        const view = this.widget.getWidgetView();
        const oItems = this._getDimensionIds(oViewId);
        if (oViewId === nViewId) {
            if (isNil(nIndex)) {
                const curr = oItems.splice(oIndex, 1);
                const preViewId = this._getPreViewIdByViewId(nViewId);
                const nItems = this._getDimensionIds(preViewId);
                nItems.push(curr[0]);
                view[oViewId] = oItems;
                view[preViewId] = nItems;
            } else {
                arrayMove(oItems, oIndex, nIndex);
                view[oViewId] = oItems;
            }
        } else {
            const nItems = this._getDimensionIds(nViewId);
            const curr = oItems.splice(oIndex, 1);
            if (parseInt(nViewId) > parseInt(oViewId)) {
                nItems.splice(isNil(nIndex) ? 0 : (parseInt(nIndex, 10) + 1), 0, curr[0]);
            } else {
                nItems.splice(nIndex, 0, curr[0]);
            }
            view[oViewId] = oItems;
            view[nViewId] = nItems;
        }

        this.widget.setWidgetView(view);
        return this.widget.$get();
    }
}