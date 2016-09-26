import {each, some} from 'core'
import Widget from './Widget'
class Template {
    constructor(template) {
        this.template = template;
        this._widgets = {};
    }

    getWidgetById(id) {
        if (this._widgets[id]) {
            return this._widgets[id];
        }
        this._widgets[id] = new Widget(this.template.popConfig.widgets[id], id);
        return this._widgets[id];
    }

    getAllWidgetIds() {
        const res = [];
        each(this.template.popConfig.widgets, (widget, wId) => {
            if (!this.getWidgetById(wId).isControl()) {
                res.push(wId);
            }
        });
        return res;
    }

    getAllControlWidgetIds() {
        const res = [];
        each(this.template.popConfig.widgets, (widget, wId) => {
            if (this.getWidgetById(wId).isControl()) {
                res.push(wId);
            }
        });
        return res;
    }

    hasControlWidget() {
        return some(this.template.popConfig.widgets, (widget, wId) => {
            return this.getWidgetById(wId).isControl();
        })
    }
}

export default Template;