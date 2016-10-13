import {each, first, arrayMove, values, keys} from 'core'
import {Widget, Template, Dimension} from 'data'
export default class DimensionComponentHelper {
    constructor(props, context) {
        this.widget = new Widget(props.$widget);
        this.wId = props.wId;
        this.dId = props.dId;
        this.dimension = this.widget.getDimensionOrTargetById(this.dId);
    }

    isUsed() {
        return this.dimension.isUsed();
    }

    switchSelect() {
        return this.dimension.setUsed(!this.isUsed());
    }
}