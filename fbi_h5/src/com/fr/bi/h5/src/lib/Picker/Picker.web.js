'use strict';

import React, {Component, PropTypes} from 'react';
import ReactDOM from 'react-dom';
import mixin from 'react-mixin'
import ReactPureRenderMixin from 'react-addons-pure-render-mixin';
import Easing from 'animated/lib/Easing';
import Animated from '../Animated/Animated.web'
import Text from '../Text/Text.web';
import View from '../View/View.web';
import PanResponder from '../PanResponder/PanResponder.web';
import StyleSheet from '../StyleSheet/StyleSheet.web';

import {translateDOMPositionXY} from 'core';
require('./Picker.css');

const PICKER = 'picker';
const PICKER_ITEM_HEIGHT = 36;

class Picker extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            selectedValue: props.selectedValue,
            trans: new Animated.ValueXY()
        }
    }

    static propTypes = {
        onValueChange: PropTypes.func,
        selectedValue: PropTypes.any
    };

    componentWillMount() {
        this._panResponder = PanResponder.create({
            onStartShouldSetPanResponder: ()=>true,
            onMoveShouldSetPanResponder: ()=>true,
            onPanResponderGrant: this._handlePanResponderGrant.bind(this),
            // onPanResponderMove: this._handlePanResponderMove.bind(this),
            onPanResponderMove: Animated.event([null, {
                dy: this.state.trans.y
            }]),
            onPanResponderRelease: this._handlePanResponderEnd.bind(this),
            onPanResponderTerminate: this._handlePanResponderEnd.bind(this)
        });
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            selectedValue: nextProps.selectedValue
        }, ()=> {
            this._moveToValue(this.state.selectedValue);
        });
    }

    componentDidMount() {
        this._moveToValue(this.state.selectedValue);
    }

    render() {
        let childDisplay = [];
        React.Children.forEach(this.props.children, (child, index)=> {
            const style = [styles.item];
            if (this.state.selectedValue === child.props.value) {
                style.push(styles.selected);
            }
            childDisplay.push(
                <View className={''} key={index} style={style} onClick={()=>this._moveToValue(child.props.value)}>
                    {child}
                </View>
            )
        });
        return (
            <View
                className={'Picker'}
                ref={PICKER}
                {...this.props}
                style={[styles.picker, this.props.style]} {...this._panResponder.panHandlers}>
                <View style={styles.highlight}></View>
                <Animated.View className={''} style={[{
                    transform: [{
                      translateY: this.state.trans.y
                    }]
                },styles.container]}>
                    {childDisplay}
                </Animated.View>
            </View>
        );
    }

    _moveToValue(selectedValue) {
        const {topOffset} = this._getOffsetPosition();
        let y = topOffset + this._getPositionByValue(selectedValue);
        let abort = false;
        Animated.timing(this.state.trans.y, {
            toValue: y,
            easing: Easing.out(Easing.ease),
            duration: 300
        }).start(endState => {
            if (!endState.finished) {
                abort = true;
            }
            if (endState.finished && !abort) {
                if (selectedValue !== this.state.selectedValue) {
                    this.setState({selectedValue: selectedValue});
                    this.props.onValueChange && this.props.onValueChange(selectedValue);
                }
            }
        });
    }

    _handlePanResponderGrant(e, gestureState) {
        this.state.trans.setOffset({x: 0, y: this.state.trans.y.__getAnimatedValue()});
        this.state.trans.setValue({x: 0, y: 0});
    }

    _handlePanResponderEnd(e, gestureState) {
        this.state.trans.flattenOffset();
        const {topOffset} = this._getOffsetPosition();
        const contentHeight = React.Children.count(this.props.children) * PICKER_ITEM_HEIGHT;
        const bottomOffset = topOffset + PICKER_ITEM_HEIGHT - contentHeight;
        let toValue = Math.round((this.state.trans.y.__getAnimatedValue() - topOffset + gestureState.vy * 100) / PICKER_ITEM_HEIGHT) * PICKER_ITEM_HEIGHT + topOffset;
        let abort = false;
        if (toValue < bottomOffset) {
            toValue = bottomOffset;
        }
        if (toValue > topOffset) {
            toValue = topOffset;
        }

        const value = this._getValueByPosition(toValue - topOffset);

        Animated.timing(this.state.trans.y, {
            toValue: toValue,
            easing: Easing.out(Easing.ease),
            duration: 300
        }).start(endState => {
            if (!endState.finished) {
                abort = true;
            }
            if (endState.finished && !abort) {
                if (value !== null && value !== this.state.selectedValue) {
                    this.setState({selectedValue: value});
                    this.props.onValueChange && this.props.onValueChange(value);
                }
            }
        });
    }

    _getOffsetPosition() {
        const dom = ReactDOM.findDOMNode(this.refs[PICKER]);
        return {
            topOffset: (dom.clientHeight - PICKER_ITEM_HEIGHT) / 2,
            clientHeight: dom.clientHeight
        };
    }

    _getPositionByValue(value) {
        let pos = 0;
        React.Children.forEach(this.props.children, (child, index)=> {
            if (child.props.value === value) {
                pos = index * PICKER_ITEM_HEIGHT;
            }
        });
        return -1 * pos;
    }

    _getValueByPosition(pos) {
        let value = null;
        const idx = Math.floor(Math.abs(pos / PICKER_ITEM_HEIGHT));
        React.Children.forEach(this.props.children, (child, index)=> {
            if (index === idx) {
                value = child.props.value;
            }
        });
        return value;
    }
}
mixin.onClass(Picker, ReactPureRenderMixin);

const styles = StyleSheet.create({
    picker: {
        overflow: 'hidden',
        position: 'relative',
        textAlign: 'center'
    },

    container: {

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
    },

    item: {
        height: PICKER_ITEM_HEIGHT,
        paddingLeft: 10,
        paddingRight: 10,
        color: '#979daa'
    },

    selected: {
        color: '#474b51'
    }
});

class Item extends Component {
    static propTypes = {
        value: PropTypes.any,
        label: PropTypes.string
    };

    render() {
        return (
            <Text key={this.props.value} style={{lineHeight: PICKER_ITEM_HEIGHT}}>{this.props.label}</Text>
        )
    }
}
Picker.Item = Item;
Picker.isReactNativeComponent = true;

export default Picker;
