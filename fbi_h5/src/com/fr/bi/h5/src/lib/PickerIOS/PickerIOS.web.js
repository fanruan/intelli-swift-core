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
require('./PickerIOS.css');

const PICKER = 'picker';
const PICKER_ITEM_HEIGHT = 36;

class Picker extends Component {

    constructor(props, context) {
        super(props, context);
        this.trans = new Animated.ValueXY();
        this.state = {
            selectedValue: props.selectedValue
        }
    }

    static propTypes = {
        onValueChange: PropTypes.func,
        selectedValue: PropTypes.any
    };

    _calculateState(props, oldState) {
        const {selectedValue} = props;
        const {defaultOffset, clientHeight} = this._getOffsetPosition();
        const contentHeight = React.Children.count(this.props.children) * PICKER_ITEM_HEIGHT;
        const maxScrollY = -defaultOffset + contentHeight - PICKER_ITEM_HEIGHT;
        const scrollY = -defaultOffset + this._getPositionByValue(selectedValue);
        return {
            selectedValue,
            defaultOffset,
            contentHeight,
            scrollY,
            maxScrollY
        }
    }

    componentWillMount() {
        this._panResponder = PanResponder.create({
            onStartShouldSetPanResponder: this._handleStartShouldSetPanResponder.bind(this),
            onMoveShouldSetPanResponder: ()=>true,
            onPanResponderGrant: this._handlePanResponderGrant.bind(this),
            onPanResponderMove: this._handlePanResponderMove.bind(this),
            onPanResponderRelease: this._handlePanResponderEnd.bind(this),
            onPanResponderTerminate: this._handlePanResponderEnd.bind(this)
        });
    }

    componentWillReceiveProps(nextProps) {
        this.setState(this._calculateState(nextProps, this.state), ()=> {
            this._moveToValue(this.state.selectedValue);
        });
    }

    componentDidMount() {
        this.setState(this._calculateState(this.props), ()=> {
            this._moveToValue(this.state.selectedValue);
        });
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
                <Animated.View
                    className={''}
                    style={[{
                    transform: [{
                        translateX: this.trans.x
                    }, {
                        translateY: this.trans.y
                    }, {
                        translateZ: 0
                    }]
                },styles.container]}>
                    {childDisplay}
                </Animated.View>
            </View>
        );
    }

    _moveToValue(value) {
        let y = -this.state.defaultOffset + this._getPositionByValue(value);

        var abort = false;
        Animated.timing(this.trans.y, {
            toValue: -y,
            easing: Easing.out(Easing.ease),
            duration: 300
        }).start(endState => {
            if (!endState.finished) {
                abort = true;
            }
            if (endState.finished && !abort) {
                if (value !== this.state.selectedValue) {
                    this.setState({selectedValue: value, scrollY: y});
                    this.props.onValueChange && this.props.onValueChange(value);
                }
            }
        });
    }

    _handleStartShouldSetPanResponder(e) {
        return e.nativeEvent.target === ReactDOM.findDOMNode(this.refs[PICKER])
    }

    _handlePanResponderGrant(e, gestureState) {
        this.trans.setOffset({x: 0, y: this.trans.y.__getAnimatedValue()});
        this.trans.setValue({x: 0, y: 0});
        e.stopPropagation();
        e.preventDefault();
    }

    _handlePanResponderMove(e, gestureState) {
        let scrollY = this.state.scrollY;
        const minScrollY = -this.state.defaultOffset, maxScrollY = this.state.maxScrollY;

        var dy = gestureState.dy;

        scrollY -= dy;
        if (scrollY < minScrollY) {
            scrollY = minScrollY - Math.pow(minScrollY - scrollY, 0.8);
        }
        if (scrollY > maxScrollY) {
            scrollY = maxScrollY + Math.pow(scrollY - maxScrollY, 0.8);
        }
        this.trans.setValue({x: 0, y: this.state.scrollY - scrollY});
        e.stopPropagation();
        e.preventDefault();
    }

    _handlePanResponderEnd(e, gestureState) {
        this.trans.flattenOffset();
        let toValue = Math.round((this.trans.y.__getAnimatedValue() - this.state.defaultOffset + gestureState.vy * 100)
                / PICKER_ITEM_HEIGHT) * PICKER_ITEM_HEIGHT + this.state.defaultOffset;
        if (toValue < -this.state.maxScrollY) {
            toValue = -this.state.maxScrollY;
        }
        if (toValue > this.state.defaultOffset) {
            toValue = this.state.defaultOffset;
        }

        const value = this._getValueByPosition(toValue - this.state.defaultOffset);
        var abort = false;
        Animated.timing(this.trans.y, {
            toValue: toValue,
            easing: Easing.out(Easing.ease),
            duration: 300
        }).start(endState => {
            if (!endState.finished) {
                abort = true;
            }
            if (endState.finished && !abort) {
                if (value !== this.state.selectedValue) {
                    this.setState({selectedValue: value, scrollY: -toValue});
                    this.props.onValueChange && this.props.onValueChange(value);
                }
            }
        });
    }

    _getOffsetPosition() {
        const dom = ReactDOM.findDOMNode(this.refs[PICKER]);
        return {
            defaultOffset: (dom.clientHeight - PICKER_ITEM_HEIGHT) / 2,
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
        return pos;
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
        transitionDuration: '0ms',
        transitionTimingFunction: 'ease-out'
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
