import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'
import Immutable from 'immutable'

import {
    ReactComponentWithPureRenderMixin,
    ReactComponentWithImmutableRenderMixin,
    cn,
    sc,
    math,
    isNil,
    emptyFunction,
    shallowEqual,
    immutableShallowEqual,
    isEqual,
    isEmpty,
    each,
    map,
    clone,
    deepClone,
    translateDOMPositionXY,
    requestAnimationFrame
} from 'core'
import React, {
    Component,
    PropTypes,
    StyleSheet,
    Text,
    Portal,
    PixelRatio,
    ListView,
    View,
    Fetch,
    Promise,
    TouchableHighlight
} from 'lib'

import {Colors, Sizes, TemplateFactory, WidgetFactory, DimensionFactory} from 'data'

import {Layout, CenterLayout, HorizontalCenterLayout, VerticalCenterLayout} from 'layout';

import {Button, TextButton, IconButton, Table} from 'base'

import {MultiSelectorWidget} from 'widgets'


class TableComponent extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {};

    state = {};

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        return <View style={styles.wrapper}>

        </View>
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
mixin.onClass(TableComponent, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    wrapper: {}
});
export default TableComponent
