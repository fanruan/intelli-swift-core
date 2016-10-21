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

    getType(){
        return this.$dimension.get('type');
    }

    getName() {
        return this.$dimension.get('name');
    }

    isUsed() {
        return this.$dimension.get('used');
    }

    getSortTarget() {
        const $sort = this.$dimension.get('sort');
        if ($sort) {
            return $sort.get('sort_target');
        }
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

    setSortTarget(dId) {
        this.$dimension = this.$dimension.setIn(['sort', 'sort_target'], dId);
        return this;
    }

    getGroupType(){
        const $group = this.$dimension.get('group');
        if($group){
            const type = $group.get('type');
            if(!isNil(type)){
                return type;
            }
        }
    }

    getGroup(){
        const $group = this.$dimension.get('group');
        if($group){
            return $group.toJS();
        }
    }

    getFilterValue(){
        const $filter = this.$dimension.get('filter_value');
        if($filter){
            return $filter.toJS();
        }
    }

    getFieldId(){
        const $src = this.$dimension.get('_src');
        if($src){
            const field_id = $src.get('field_id');
            if(!isNil(field_id)){
                return field_id;
            }
        }
    }

    getDimensionSrc() {
        const $src = this.$dimension.get('_src');
        if($src){
            return $src.toJS();
        }
    }
}
export default Dimension