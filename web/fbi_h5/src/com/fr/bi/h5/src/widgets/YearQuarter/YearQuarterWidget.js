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
class YearQuarterWidget extends Component {
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
        quarter: this.props.quarter
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
        const year = [], quarter = [];
        for (let i = 1900; i <= 2050; i++) {
            year.push(
                <PickerIOS.Item key={i} value={i} label={`${i + BH.i18nText("BH-Year")}`}/>
            );
        }
        for (let i = 1; i < 5; i++) {
            quarter.push(
                <PickerIOS.Item key={i} value={i} label={`${BH.i18nText("BH-Quarter_" + i)}`}/>
            )
        }
        if(this.state.selection === 1){
            detailPicker = <Layout main='center' style={styles.detailPicker}>
                    <Layout style={styles.highlight}/>
                    <PickerIOS selectedValue={this.state.year || new Date().getFullYear()} onValueChange={(Y)=> {
                        this.setState({year: Y}, ()=>{
                            const {year, quarter}=this.state;
                            this.props.onValueChange({year, quarter});
                        });
                    }}>
                        {year}
                    </PickerIOS>
            </Layout>
        }else{
            detailPicker = <Layout main='center' style={styles.detailPicker}>
                <Layout style={styles.highlight}/>
                    <PickerIOS selectedValue={this.state.quarter || new Date().getMonth()} onValueChange={(M)=> {
                        this.setState({quarter: M}, ()=>{
                            const {year, quarter} = this.state;
                            this.props.onValueChange({year, quarter})
                        });
                    }}>
                        {quarter}
                    </PickerIOS>
            </Layout>
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
                    <Text style={styles.dateLabel}>{BH.i18nText("BH-Select_Quarter")}</Text>
                    <TextButton style={styles.date} onPress = {()=>{
                this.setState({
                    selection: 2
                })
                }}>{state.quarter ? `${BH.i18nText("BH-Quarter_" + state.quarter)}` : BH.i18nText("BH-Unrestricted")}</TextButton>
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
        const {year, quarter} = nextProps;
        this.setState({year, quarter});
    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }

}
mixin.onClass(YearQuarterWidget, ReactComponentWithPureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {},

    dateLabel: {
        width: 120,
        paddingLeft: 30,
    },

    detailPicker: {
        position: 'relative'
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
        top: '50%',
        marginTop: -1 * PICKER_ITEM_HEIGHT / 2,
        borderTopWidth: 1,
        borderTopStyle: 'solid',
        borderTopColor: '#ddd',
        borderBottomWidth: 1,
        borderBottomStyle: 'solid',
        borderBottomColor: '#ddd'
    }
});
export default YearQuarterWidget