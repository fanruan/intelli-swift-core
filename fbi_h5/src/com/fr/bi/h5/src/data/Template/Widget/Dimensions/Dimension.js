import {isNil} from 'core'
class Dimension {
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
        const $sort = this.$dimension.get('sort');
        if ($sort) {
            const type = $sort.get('type');
            if (!isNil(type)) {
                return type;
            }
        }
        return BICst.SORT.ASC;
    }

    setUsed(b) {
        this.$dimension = this.$dimension.set('used', !!b);
        return this;
    }

    setSortType(type) {
        this.$dimension = this.$dimension.setIn(['sort', 'type'], type);
        return this;
    }
}
export default Dimension