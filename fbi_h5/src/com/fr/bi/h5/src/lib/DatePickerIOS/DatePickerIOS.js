'use strict';

import React, {Component, PropTypes} from 'react';
import ReactDOM from 'react-dom';
import mixin from 'react-mixin'
import ReactPureRenderMixin from 'react-addons-pure-render-mixin';
import View from '../View/View.web';
import StyleSheet from '../StyleSheet/StyleSheet.web';
import Picker from '../Picker/Picker.web'

const PICKER_ITEM_HEIGHT = 36;
const DAYS = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

class DatePickerIOS extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            Y: props.date.getFullYear(),
            M: props.date.getMonth(),
            D: props.date.getDate(),
            maximumDate: props.maximumDate,
            minimumDate: props.minimumDate,
            minuteInterval: props.minuteInterval,
            mode: props.mode
        }
    }

    static propTypes = {
        date: PropTypes.object,
        maximumDate: PropTypes.object,
        minimumDate: PropTypes.object,
        minuteInterval: PropTypes.oneOf([1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30]),
        mode: PropTypes.oneOf(['date', 'time', 'datetime']),
        onDateChange: PropTypes.func,
        timeZoneOffsetInMinutes: PropTypes.number
    };

    static defaultProps = {
        date: new Date()
    };

    componentWillMount() {
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            Y: nextProps.date.getFullYear(),
            M: nextProps.date.getMonth(),
            D: nextProps.date.getDate(),
            maximumDate: nextProps.maximumDate,
            minimumDate: nextProps.minimumDate,
            minuteInterval: nextProps.minuteInterval,
            mode: nextProps.mode
        });
    }

    componentDidMount() {
    }

    render() {
        const year = [], month = [], day = [];
        for (let i = 1900; i <= 2050; i++) {
            year.push(
                <Picker.Item value={i} label={`${i}年`}></Picker.Item>
            );
        }
        for (let i = 0; i < 12; i++) {
            month.push(
                <Picker.Item value={i} label={`${i+1}月`}></Picker.Item>
            )
        }
        const len = (this._isLeap(this.state.Y) && this.state.M === 1 ? 29 : DAYS[this.state.M]);
        if (this.state.D > len) {
            this.state.D = len;
        }
        for (let i = 1; i <= len; i++) {
            day.push(
                <Picker.Item value={i} label={`${i}日`}></Picker.Item>
            )
        }
        return (
            <View style={styles.datepicker}>
                <View style={styles.highlight}></View>
                <View style={styles.container}>
                    <Picker selectedValue={this.state.Y} style={styles.picker} onValueChange={(Y)=>{
                        this.setState({Y})
                        this.props.onDateChange(new Date(this.state.Y,this.state.M,this.state.D))
                    }}>
                        {year}
                    </Picker>
                    <Picker selectedValue={this.state.M} style={styles.picker} onValueChange={(M)=>{
                        this.setState({M})
                        this.props.onDateChange(new Date(this.state.Y,this.state.M,this.state.D))
                    }}>
                        {month}
                    </Picker>
                    <Picker selectedValue={this.state.D} style={styles.picker} onValueChange={(D)=>{
                        this.setState({D})
                        this.props.onDateChange(new Date(this.state.Y,this.state.M,this.state.D))
                    }}>
                        {day}
                    </Picker>
                </View>
            </View>
        );
    }

    _isLeap(year) {
        return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0;
    }

}
mixin.onClass(DatePickerIOS, ReactPureRenderMixin);

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
DatePickerIOS.isReactNativeComponent = true;

export default DatePickerIOS;
