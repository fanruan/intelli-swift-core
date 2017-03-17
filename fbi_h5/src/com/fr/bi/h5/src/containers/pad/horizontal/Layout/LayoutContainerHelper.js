import {each} from 'core'
import {TemplateFactory, WidgetFactory} from 'data'
import LayoutContainerLayoutHelper from './LayoutContainerLayoutHelper'
export default class LayoutContainerHelper {
    constructor(props) {
        this.template = TemplateFactory.createTemplate(props.$template);
        this.wIds = this.template.getAllWidgetIds();
        this._layoutHelper = new LayoutContainerLayoutHelper(props);
    }

    getAllWidgetIds() {
        return this.wIds;
    }

    get$Template() {
        return this.template.$get();
    }

    get$WidgetByWidgetId(wId) {
        return this.template.get$WidgetByWidgetId(wId);
    }

    getWidgetTypeByWidgetId(wId) {
        return WidgetFactory.createWidget(this.get$WidgetByWidgetId(wId), wId, this.template).getType();
    }

    getLayoutType() {
        return this.template.getLayoutType();
    }

    getWidgetLayoutByWidgetId(wId) {
        return this._layoutHelper.getWidgetLayoutByWidgetId(wId);
    }
}