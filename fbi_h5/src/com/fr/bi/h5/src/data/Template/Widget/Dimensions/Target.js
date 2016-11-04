import {isNil} from 'core'
class Target {
    constructor($dimension, dId, widget) {
        this.$dimension = $dimension;
        this.dId = dId;
        this.widget = widget;
    }

    $get() {
        return this.$dimension;
    }

    getName() {
        return this.$dimension.get('name');
    }

    isUsed() {
        return this.$dimension.get('used');
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

    setUsed(b) {
        this.$dimension = this.$dimension.set('used', !!b);
        return this;
    }

    setSortType(type) {
        this.$dimension = this.$dimension.setIn(['sort', 'type'], type);
        return this;
    }

    setFilterValue(filterValue) {
        this.$dimension = this.$dimension.setIn(['filter_value'], filterValue);
        return this;
    }

    getFilterValue() {
        const $filter = this.$dimension.get('filter_value');
        return isNil($filter) ? {} : $filter.toJS();
    }

    getFieldId() {
        return this.$dimension.getIn(['_src', 'field_id']);
    }

    getDimensionSrc() {
        const $src = this.$dimension.get('_src');
        return isNil($src) ? {} : $src.toJS();
    }
}
export default Target;