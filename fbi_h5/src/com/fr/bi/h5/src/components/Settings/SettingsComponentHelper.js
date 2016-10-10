import {each} from 'core'
import {Widget, Template, Dimension} from 'data'
export default class SettingsComponentHelper {
    constructor(props, context) {
        this.widget = new Widget(props.$widget);

    }

    getDimensionsItems() {
        const result = [];
        const dimensions = this.widget.getAllDimensionAndTargetIds();
        each(dimensions, (dId)=> {
            const dim = this.widget.getDimensionOrTargetById(dId);
            result.push({
                text: dim.getName(),
                dId: dId
            })
        });
        return result;
    }
}