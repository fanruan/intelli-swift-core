import {isNil} from 'core'
import DimensionOrTarget from './DimensionOrTarget'
class Dimension extends DimensionOrTarget{
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
                return this.widget.getDimensionOrTargetById(sort_target).getName();
            }
        }
        return this.getName();
    }

    getSortType() {
        return this.$dimension.getIn(['sort', 'type']) || BICst.SORT.ASC;
    }

    getGroupType() {
        return this.$dimension.getIn(['group', 'type']);
    }

    getGroup() {
        const $group = this.$dimension.get('group');
        return isNil($group) ? {} : $group.toJS();
    }

    setFilterValue(filterValue) {
        this.$dimension = this.$dimension.setIn(['filter_value'], filterValue);
        return this;
    }

    getFilterValue() {
        const $filter = this.$dimension.get('filter_value');
        return isNil($filter) ? {} : $filter.toJS();
    }

    setSortType(type) {
        this.$dimension = this.$dimension.setIn(['sort', 'type'], type);
        return this;
    }

    setSortTarget(dId) {
        this.$dimension = this.$dimension.setIn(['sort', 'sort_target'], dId);
        return this;
    }
}
export default Dimension