/**
 * 年月控件
 * Created by Young's on 2016/10/13.
 */
import {Fetch} from 'lib'
import AbstractWidget from './AbstractWidget'

class YearQuarterControl extends AbstractWidget {
    constructor($widget, ...props) {
        super($widget, ...props);
    }

    isControl() {
        return true;
    }

    getYear() {
        return this.$widget.getIn(['value', 'year']);
    }

    getQuarter() {
        return this.$widget.getIn(['value', 'quarter']);
    }
}
export default YearQuarterControl;