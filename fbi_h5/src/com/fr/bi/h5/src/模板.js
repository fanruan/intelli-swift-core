import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
} from 'lib'

import {Grid, ScrollSync, AutoSizer} from 'base'


class TableWidget extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
    };

    state = {};

    render() {
        const {...props} = this.props;
        return <View>
        </View>
    }

}
mixin.onClass(TableWidget, PureRenderMixin);
const style = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default TableWidget
