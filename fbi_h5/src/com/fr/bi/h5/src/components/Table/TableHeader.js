import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {cn, sc, isNil, requestAnimationFrame, emptyFunction, shallowEqual, isEqual, each} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    PixelRatio,
    ListView,
    View,
    Fetch,
    Promise
} from 'lib'

import {Colors, Template} from 'data'

import {Icon, Table, AutoSizer} from 'base'

import {MultiSelectorWidget} from 'widgets'


class TableHeader extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {};

    state = {};

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps() {

    }

    componentWillUpdate() {

    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        return <View style={styles.region}>
            <Text numberOfLines={2}>{props.text}</Text>
        </View>
    }

}
mixin.onClass(TableHeader, PureRenderMixin);
const styles = StyleSheet.create({
    region: {
        padding: '0 4px 0 4px',
        width: '100%',
        height: '100%',
        justifyContent: 'center'
    }
});
export default TableHeader
