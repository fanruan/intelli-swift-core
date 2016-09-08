import Widget from './Widget'
class Template {
    constructor(template) {
        this.template = template;
        this._widgets = {};
    }

    getWidgetById(id) {
        if(this._widgets[id]){
            return this._widgets[id];
        }
        this._widgets[id] = new Widget(this.template.popConfig.widgets[id], id);
        return this._widgets[id];
    }

    getAllWidgetIds(){
        return Object.keys(this.template.popConfig.widgets);
    }
}

export default Template;