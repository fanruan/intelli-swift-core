import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'

import {cn, sc, math, isNil, requestAnimationFrame, emptyFunction, shallowEqual, isEqual, each} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    PixelRatio,
    ListView,
    View,
    Fetch,
    Promise,
    TouchableHighlight
} from 'lib'

import {Colors, Size, Template, Widget, Dimension, Target} from 'data'

import {CenterLayout, Icon, Table} from 'base'

import {MultiSelectorWidget} from 'widgets'


class TableComponent extends Component {
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

    render() {
        const {...props} = this.props, {...state} = this.state;
        return <View style={styles.wrapper}>

        </View>
    }

    shouldComponentUpdate(nextProps, nextState) {

    }

    componentWillReceiveProps(nextProps) {

    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }

}
mixin.onClass(TableComponent, PureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        flex: 1
    }
});
export default TableComponent
