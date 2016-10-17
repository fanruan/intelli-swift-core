import Dimension from './Dimension'
import Target from './Target'
export default {
    createDimension: (...props)=> {
        return new Dimension(...props);
    },

    createTarget: (...props)=> {
        return new Target(...props)
    }
};