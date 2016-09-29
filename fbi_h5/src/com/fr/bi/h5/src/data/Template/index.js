import {each, some} from 'core'
import Widget from './Widget'
class Template {
    constructor(template) {
        this.$$template = template;
    }

    get$$WidgetById(id) {
        return this.$$template.getIn(['widgets',id]);
    }

    getAllWidgetIds() {
        const res = [];
        this.$$template.get('widgets').forEach(($$widget, wId)=>{
            if (!new Widget($$widget).isControl()) {
                res.push(wId);
            }
        });
        return res;
    }

    getAllControlWidgetIds() {
        const res = [];
        this.$$template.get('widgets').forEach(($$widget, wId)=>{
            if (new Widget($$widget).isControl()) {
                res.push(wId);
            }
        });
        return res;
    }

    hasControlWidget() {
        return this.$$template.get('widgets').some(($$widget, wId)=>{
            return new Widget($$widget).isControl();
        });
        //return some(this.template.widgets, (widget, wId) => {
        //    return this.getWidgetById(wId).isControl();
        //})
    }
}

export default Template;