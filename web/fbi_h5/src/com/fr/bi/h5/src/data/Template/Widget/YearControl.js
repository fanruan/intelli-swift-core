/**
 * 年份控件
 * Created by windy on 2016/10/13.
 */
import {Fetch} from 'lib'
import AbstractWidget from './AbstractWidget'

class YearControl extends AbstractWidget {
    constructor($widget, ...props) {
        super($widget, ...props);
    }

    isControl() {
        return true;
    }

    getYear() {
        return this.$widget.get('value');
    }
}
export default YearControl;