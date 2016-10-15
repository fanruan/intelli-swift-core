import {each} from 'core'
import {WidgetFactory, DimensionFactory} from 'data'

export default class TargetSortComponentHelper {
    constructor(props, context) {
        this.widget = WidgetFactory.createWidget(props.$widget, props.wId);
        this.dId = props.dId;
        this.dimension = this.widget.getDimensionOrTargetById(props.dId);
    }

    getSortType() {
        return this.widget.getSortType();
    }

    setSortType(type) {
        this.widget.setSortType(type);
        this.widget.setSortTarget(this.dId);
        return this.widget.$get();
    }
}