import {isNil} from 'core'
class DimensionOrTarget {
    constructor($dimension, dId, widget) {
        this.$dimension = $dimension;
        this.dId = dId;
        this.widget = widget;
    }

    $get() {
        return this.$dimension;
    }

    getType() {
        return this.$dimension.get('type');
    }

    getName() {
        return this.$dimension.get('name');
    }

    getFieldId() {
        return this.$dimension.getIn(['_src', 'field_id']);
    }

    getDimensionSrc() {
        const $src = this.$dimension.get('_src');
        return isNil($src) ? {} : $src.toJS();
    }

    isUsed() {
        return this.$dimension.get('used');
    }

    setUsed(b) {
        this.$dimension = this.$dimension.set('used', !!b);
        return this;
    }
}
export default DimensionOrTarget