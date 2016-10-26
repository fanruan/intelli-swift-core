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
    YearPicker,
    MonthPicker,
    TouchableHighlight
} from 'lib'

import {Colors, Sizes, TemplateFactory, WidgetFactory, DimensionFactory} from 'data'

import {Layout, CenterLayout, HorizontalCenterLayout, VerticalCenterLayout} from 'layout';

import {Button, TextButton, IconButton, Table} from 'base'

import {PickerIOS} from 'lib'

import {MultiSelectorWidget} from 'widgets'

const PICKER_ITEM_HEIGHT = 36;
class YearWidget extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        onValueChange: emptyFunction
    };

    state = {
        year: this.props.year
    };

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        var detailPicker;
        const year = [], month = [];
        for (let i = 1900; i <= 2050; i++) {
            year.push(
                <PickerIOS.Item key={i} value={i} label={`${i + BH.i18nText("BH-Year")}`}/>
            );
        }
        return <Layout dir='top' main='justify' style={styles.wrapper}>
            <Layout cross='center' style={styles.button}>
                <Text style={styles.dateLabel}>{BH.i18nText("BH-Select_Year")}</Text>
                <Text style={styles.date}>{state.year ? `${state.year + BH.i18nText("BH-Year")}` : BH.i18nText("BH-Unrestricted")}</Text>
            </Layout>
            <Layout dir='top' box='first' style={styles.picker}>
                <CenterLayout style={styles.header}>
                    <Text>{BH.i18nText("BH-Select_Date")}</Text>
                </CenterLayout>
                <Layout main="center" cross="center">
                    <Layout style={styles.highlight}/>
                        <PickerIOS selectedValue={this.state.year || new Date().getFullYear()} onValueChange={(Y)=> {
                        this.setState({year: Y}, ()=>{
                            const {year, month}=this.state;
                            this.props.onValueChange({year, month});
                        });
                    }}>
                            {year}
                        </PickerIOS>
                </Layout>
            </Layout>
        </Layout>
    }

    componentWillReceiveProps(nextProps) {
        const {year, month} = nextProps;
        this.setState({year, month});
    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }

}
mixin.onClass(YearWidget, ReactComponentWithPureRenderMixin);
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
    },
    highlight: {
        position: 'absolute',
        left: 0,
        right: 0,
        height: PICKER_ITEM_HEIGHT,
        borderTopWidth: 1,
        borderTopStyle: 'solid',
        borderTopColor: '#ddd',
        borderBottomWidth: 1,
        borderBottomStyle: 'solid',
        borderBottomColor: '#ddd'
    }
});
export default YearWidget