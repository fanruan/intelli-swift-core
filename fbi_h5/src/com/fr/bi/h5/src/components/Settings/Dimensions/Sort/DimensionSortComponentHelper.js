import {each} from 'core'
import {WidgetFactory, DimensionFactory} from 'data'

export default class DimensionSortComponentHelper {
    constructor(props, context) {
        this.widget = WidgetFactory.createWidget(props.$widget, props.wId);
        this.dId = props.dId;
        this.dimension = this.widget.getDimensionOrTargetById(props.dId);
    }

    getSortType() {
        return this.dimension.getSortType();
    }

    getSortTargetValue() {
        return this.dimension.getSortTarget();
    }

    //指标没有sort_target
    hasSortTargetItems() {
        return this.widget.isDimensionById(this.dId);
    }

    getSortTargetItems() {
        const name = this.dimension.getName();
        const result = [{value: this.dId, label: name}];
        each(this.widget.getAllTargetIds(), (dId)=> {
            const name = this.widget.getTargetById(dId).getName();
            result.push({
                value: dId,
                label: name
            })
        });
        return result;
    }

    setSortType(type) {
        this.dimension.setSortType(type);
        this.widget.set$Dimension(this.dimension.$get(), this.dId);
        return this.widget.$get();
    }

    setSortTarget(dId) {
        this.dimension.setSortTarget(dId);
        this.widget.set$Dimension(this.dimension.$get(), this.dId);
        return this.widget.$get();
    }
}