import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import React, {
    Component,
    StyleSheet
} from 'lib'

import Item from './Item'

class List extends Component {
    propTypes = {

    }

    constructor(props, context) {
        super(props, context);

    }

    render() {

    }
}
List.Item = Item;
mixin.onClass(List, PureRenderMixin);
export default List
