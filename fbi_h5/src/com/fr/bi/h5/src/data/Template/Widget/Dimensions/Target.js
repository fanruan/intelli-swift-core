import {isNil} from 'core'
import DimensionOrTarget from './DimensionOrTarget'
class Target extends DimensionOrTarget{
    constructor($dimension, dId, widget) {
        super($dimension, dId, widget);
    }

    getSortTarget() {
        return this.$dimension.getIn(['sort', 'sort_target']);
    }

    getSortTargetName() {
        const $sort = this.$dimension.get('sort');
        if ($sort) {
            const sort_target = $sort.get('sort_target');
            if (sort_target) {
                return this.widget.getDimensionByDimensionId(sort_target).getName();
            }
        }
        return this.getName();
    }

    getSortType() {
        return this.$dimension.getIn(['sort', 'type']) || BICst.SORT.ASC;
    }

    getFilterValue() {
        const $filter = this.$dimension.get('filter_value');
        return isNil($filter) ? {} : $filter.toJS();
    }

    setSortType(type) {
        this.$dimension = this.$dimension.setIn(['sort', 'type'], type);
        return this;
    }

    setFilterValue(filterValue) {
        this.$dimension = this.$dimension.setIn(['filter_value'], filterValue);
        return this;
    }
}
export default Target;