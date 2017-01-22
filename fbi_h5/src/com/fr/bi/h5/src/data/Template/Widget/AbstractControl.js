import Immutable from 'immutable'
import {each, invariant, isNil, find, findKey, remove, isEqual, size, keys, isNumeric, isString} from 'core';
import {Fetch} from 'lib'
import AbstractWidget from './AbstractWidget'
import DimensionFactory from './Dimensions/DimensionFactory'

class AbstractControl extends AbstractWidget {
    constructor($widget, wId, template) {
        super($widget, wId, template)
    }

    isControl() {
        return true;
    }

    isQueryControl() {
        return false;
    }
}

export default AbstractControl;