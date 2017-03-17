import {each} from 'core'
import {TemplateFactory, WidgetFactory} from 'data'
export default class LayoutContainerHelper {
    constructor(props) {
        this.template = TemplateFactory.createTemplate(props.$template);
        this.wIds = this.template.getAllWidgetIds();
        this.sorted = this._getAllSortedStatisticWidgetIds();
    }

    _getWidgetBoundsByWidgetId(wId) {
        const widget = this.template.getWidgetByWidgetId(wId);
        return widget.getWidgetBounds();
    }

    _isEqual(num1, num2) {
        return Math.abs(num1 - num2) < 2;
    }

    _getAllSortedStatisticWidgetIds() {
        const wIds = this.template.getAllStatisticWidgetIds();
        return wIds.sort((wId1, wId2)=> {
            const r1 = this._getWidgetBoundsByWidgetId(wId1);
            const r2 = this._getWidgetBoundsByWidgetId(wId2);
            if (this._isEqual(r1.top, r2.top)) {
                return r1.left - r2.left;
            }
            return r1.top - r2.top;
        });
    }

    getAllSortedStatisticWidgetIds() {
        return this.sorted;
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
}