/**
 * 文本控件
 * Created by Young's on 2016/10/13.
 */
import {Fetch} from 'lib'
import AbstractControl from './AbstractControl'

class QueryControl extends AbstractControl {
    constructor($widget, ...props) {
        super($widget, ...props);
    }

    isQueryControl() {
        return true;
    }
}
export default QueryControl;