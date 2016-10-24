'use strict';

import React, {Component, PropTypes} from 'react';
import ReactDOM from 'react-dom';
import mixin from 'react-mixin'
import ReactPureRenderMixin from 'react-addons-pure-render-mixin';
import View from '../View/View.web';
import StyleSheet from '../StyleSheet/StyleSheet.web';
import Picker from '../PickerIOS/PickerIOS.web'

const PICKER_ITEM_HEIGHT = 36;

class MonthPicker extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            month: props.month
        }
    }

    static propTypes = {
        month: PropTypes.number,
        onMonthChange: PropTypes.func,
        timeZoneOffsetInMinutes: PropTypes.number
    };

    static defaultProps = {
        month: new Date().getMonth()
    };

    componentWillMount() {
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            month: nextProps.month
        });
    }

    componentDidMount() {
    }

    render() {
        const year = [], month = [], day = [];
        for (let i = 0; i < 12; i++) {
            month.push(
                <Picker.Item key={i} value={i} label={`${i + 1}月`}/>
            )
        }
        return (
            <View style={styles.datepicker}>
                <View style={styles.highlight}/>
                <View style={styles.container}>
                    <Picker selectedValue={this.state.month} style={styles.picker} onValueChange={(month)=> {
                        this.setState({month});
                        this.props.onMonthChange(this.state.month)
                    }}>
                        {month}
                    </Picker>
                </View>
            </View>
        );
    }
}
mixin.onClass(MonthPicker, ReactPureRenderMixin);

const styles = StyleSheet.create({
    datepicker: {
        flexDirection: 'row',
        justifyContent: 'center'
    },
    container: {
        flexDirection: 'row'
    },
    picker: {},
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
MonthPicker.isReactNativeComponent = true;

export default MonthPicker;