import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {cn, sc, requestAnimationFrame, emptyFunction, shallowEqual, isEqual} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
} from 'lib'

import {Colors, Template} from 'data'

import {Icon, Table, AutoSizer} from 'base'

import {MultiSelectorWidget} from 'widgets'


class TableWidget extends Component {
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
        return <View></View>
    }

}
mixin.onClass(TableWidget, PureRenderMixin);
const styles = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default TableWidget
