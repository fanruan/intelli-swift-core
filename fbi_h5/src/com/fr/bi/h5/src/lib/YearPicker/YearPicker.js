'use strict';

import React, {Component, PropTypes} from 'react';
import ReactDOM from 'react-dom';
import mixin from 'react-mixin'
import ReactPureRenderMixin from 'react-addons-pure-render-mixin';
import View from '../View/View.web';
import StyleSheet from '../StyleSheet/StyleSheet.web';
import Picker from '../PickerIOS/PickerIOS.web'

const PICKER_ITEM_HEIGHT = 36;

class YearPicker extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            year: props.year
        }
    }

    static propTypes = {
        year: PropTypes.number,
        onYearChange: PropTypes.func,
        timeZoneOffsetInMinutes: PropTypes.number
    };

    static defaultProps = {
        year: new Date().getFullYear()
    };

    componentWillMount() {
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            year: nextProps.year
        });
    }

    componentDidMount() {
    }

    render() {
        const year = [], month = [], day = [];
        for (let i = 1900; i <= 2050; i++) {
            year.push(
                <Picker.Item key={i} value={i} label={`${i}年`}/>
            );
        }
        return (
            <View style={styles.datepicker}>
                <View style={styles.highlight}/>
                <View style={styles.container}>
                    <Picker selectedValue={this.state.year} style={styles.picker} onValueChange={(year)=> {
                        this.setState({year});
                        this.props.onYearChange(this.state.year)
                    }}>
                        {year}
                    </Picker>
                </View>
            </View>
        );
    }

}
mixin.onClass(YearPicker, ReactPureRenderMixin);

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
YearPicker.isReactNativeComponent = true;

export default YearPicker;