import {each, first, arrayMove, values, keys} from 'core'
import {Widget, Template, Dimension} from 'data'
export default class SettingsComponentHelper {
    constructor(props, context) {
        this.widget = new Widget(props.$widget);

    }

    getDimensionsItems() {
        const result = [];
        each(this._getDimensionIds(), (dId)=> {
            const dim = this.widget.getDimensionOrTargetById(dId);
            result.push({
                text: dim.getName(),
                dId: dId
            })
        });
        return result;
    }

    _getDimensionIds() {
        const view = this.widget.getWidgetView();
        return values(view)[0];
    }

    doMove(oldIndex, newIndex) {
        const view = this.widget.getWidgetView();
        const items = this._getDimensionIds();
        arrayMove(items, oldIndex, newIndex);
        view[keys(view)[0]] = items;
        this.widget.setWidgetView(view);
        return this.widget.$get();
    }
}