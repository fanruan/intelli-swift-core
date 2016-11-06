import {each} from 'core'
import {TemplateFactory, WidgetFactory} from 'data'
export default class LayoutContainerLayoutHelper {
    constructor(props) {
        this.width = props.width;
        this.height = props.height;
        this.template = TemplateFactory.createTemplate(props.$template);
        this.wIds = this.template.getAllWidgetIds();
        this.layoutType = this.getLayoutType();
        this.layoutRatio = this.getLayoutRatio();
        const size = this._getContainerSize();
        this.widthRatio = (this.width / this.layoutRatio.x) / size.width;
    }

    _getContainerSize() {
        let width = 0, height = 0;
        each(this.wIds, (wId)=> {
            const bounds = this.getWidgetBoundsByWidgetId(wId);
            width = Math.max(width, bounds.left + bounds.width);
            height = Math.max(height, bounds.top + bounds.height);
        });
        return {
            width,
            height
        }
    }

    getLayoutType() {
        return this.template.getLayoutType();
    }

    getLayoutRatio() {
        return this.template.getLayoutRatio();
    }

    getWidgetBoundsByWidgetId(wId) {
        const widget = this.template.getWidgetByWidgetId(wId);
        return widget.getWidgetBounds();
    }

    getWidgetLayoutByWidgetId(wId) {
        const bounds = this.getWidgetBoundsByWidgetId(wId);
        return {
            top: bounds.top,
            height: bounds.height,
            left: bounds.left * this.widthRatio,
            width: bounds.width * this.widthRatio
        }
    }
}