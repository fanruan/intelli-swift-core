import {each, some} from 'core'
import Widget from './Widget'
class Template {
    constructor(template) {
        this.$template = template;
    }

    $get() {
        return this.$template;
    }

    get$$WidgetById(id) {
        return this.$template.getIn(['widgets', id]);
    }

    getWidgetById(id) {
        return new Widget(this.get$$WidgetById(id));
    }

    getAllWidgetIds() {
        const res = [];
        this.$template.get('widgets').forEach(($widget, wId)=> {
            if (!new Widget($widget).isControl()) {
                res.push(wId);
            }
        });
        return res;
    }

    getAllControlWidgetIds() {
        const res = [];
        this.$template.get('widgets').forEach(($widget, wId)=> {
            if (new Widget($widget).isControl()) {
                res.push(wId);
            }
        });
        return res;
    }

    hasControlWidget() {
        return this.$template.get('widgets').some(($widget, wId)=> {
            return new Widget($widget).isControl();
        });
    }

    setWidget(id, widget) {
        this.$template = this.$template.setIn(['widgets', id], widget.$get());
        return this;
    }
}

export default Template;