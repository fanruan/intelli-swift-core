import Dimension from './Dimension'
import Target from './Target'
export default {
    createDimension: ($dimension, dId, widget)=> {
        if (widget.isDimDimensionByDimensionId(dId)) {
            return new Dimension($dimension, dId, widget);
        }
        return new Target($dimension, dId, widget);
    }
};