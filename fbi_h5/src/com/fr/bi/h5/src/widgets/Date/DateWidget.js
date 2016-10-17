import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'
import Immutable from 'immutable'

import {
    ReactComponentWithPureRenderMixin, ReactComponentWithImmutableRenderMixin,
    cn, sc, math, isNil, emptyFunction, shallowEqual, immutableShallowEqual, isEqual, isEmpty, each, map,
    translateDOMPositionXY, requestAnimationFrame
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
    DatePickerIOS,
    TouchableHighlight
} from 'lib'

import {Colors, Sizes, TemplateFactory, WidgetFactory, DimensionFactory} from 'data'

import {Layout, CenterLayout, HorizontalCenterLayout, VerticalCenterLayout} from 'layout';

import {Button, TextButton, IconButton, Table} from 'base'

import {MultiSelectorWidget} from 'widgets'


class DateWidget extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        onValueChange: emptyFunction
    };

    state = {
        year: this.props.year,
        month: this.props.month,
        day: this.props.day,
    };

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        return <Layout dir='top' main='justify' style={styles.wrapper}>
            <Layout cross='center' style={styles.button}>
                <Text style={styles.dateLabel}>选择日期</Text>
                <Text
                    style={styles.date}>{state.year ? `${state.year}年${state.month + 1}月${state.day}日` : '无限制'}</Text>
            </Layout>
            <Layout dir='top' box='first' style={styles.picker}>
                <CenterLayout style={styles.header}>
                    <Text>{'选择日期'}</Text>
                </CenterLayout>
                <DatePickerIOS date={state.year ? new Date(state.year, state.month, state.day) : new Date()}
                               onDateChange={(date)=> {
                                   this.setState({
                                       year: date.getFullYear(),
                                       month: date.getMonth(),
                                       day: date.getDate()
                                   }, ()=> {
                                       const {year, month, day}=this.state;
                                       this.props.onValueChange({year, month, day});
                                   });
                               }}/>
            </Layout>
        </Layout>
    }

    componentWillReceiveProps(nextProps) {
        const {year, month, day} = nextProps;
        this.setState({year, month, day});
    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }

}
mixin.onClass(DateWidget, ReactComponentWithPureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {},

    dateLabel: {
        width: 120,
        paddingLeft: 30,
    },

    button: {
        height: Sizes.HEADER_HEIGHT
    },

    date: {
        paddingLeft: 20,
    },

    picker: {
        height: 217
    },

    header: {
        paddingLeft: 20,
        paddingRight: 20,
        height: Sizes.HEADER_HEIGHT,
        borderBottomColor: Colors.BORDER,
        borderBottomWidth: 1 / PixelRatio.get()
    }
});
export default DateWidget
