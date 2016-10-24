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

import {MultiSelectorWidget} from 'widgets'


class YearMonthWidget extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        onValueChange: emptyFunction
    };

    state = {
        selection: 1,
        year: this.props.year,
        month: this.props.month
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
        if(this.state.selection === 1){
            detailPicker = <YearPicker year={state.year || new Date().getFullYear()}
                                       onYearChange={(Y)=> {
            this.setState({
                year: Y
            }, ()=> {
                const {year, month}=this.state;
                this.props.onValueChange({year, month});
            });
        }}/>
        }else{
            detailPicker = <MonthPicker month={state.month || new Date().getMonth()}
                                       onMonthChange={(M)=> {
            this.setState({
                month: M
            }, ()=> {
                const {year, month}=this.state;
                this.props.onValueChange({year, month});
            });
        }}/>
        }
        return <Layout dir='top' main='justify' style={styles.wrapper}>
            <Layout cross='center' style={styles.button}>
                <Layout>
                    <Text style={styles.dateLabel}>{BH.i18nText("BH-Select_Year")}</Text>
                    <TextButton style={styles.date} onPress = {()=>{
                this.setState({
                    selection: 1
                })
                }}>{state.year ? `${state.year + BH.i18nText("BH-Year")}` : BH.i18nText("BH-Unrestricted")}</TextButton>
                </Layout>
                <Layout>
                    <Text style={styles.dateLabel}>{BH.i18nText("BH-Select_Month")}</Text>
                    <TextButton style={styles.date} onPress = {()=>{
                this.setState({
                    selection: 2
                })
                }}>{state.month ? `${(state.month + 1) + BH.i18nText("BH-Month")}` : BH.i18nText("BH-Unrestricted")}</TextButton>
                </Layout>
            </Layout>
            <Layout dir='top' box='first' style={styles.picker}>
                <CenterLayout style={styles.header}>
                    <Text>{BH.i18nText("BH-Select_Date")}</Text>
                </CenterLayout>
                {detailPicker}
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
mixin.onClass(YearMonthWidget, ReactComponentWithPureRenderMixin);
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
export default YearMonthWidget