/**
 * Copyright (c) 2015-present, Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * Copyright (c) 2015, Facebook, Inc.  All rights reserved.
 *
 * @providesModule ReactTouchableWithoutFeedback
 */
'use strict';

import '../PanResponder/PanResponder.web';
import React, { Component } from 'react';
import Touchable from './Touchable';
import mixin from 'react-mixin';

/**
 * When the scroll view is disabled, this defines how far your touch may move
 * off of the button, before deactivating the button. Once deactivated, try
 * moving it back and you'll see that the button is once again reactivated!
 * Move it back and forth several times while the scroll view is disabled.
 */
var PRESS_RECT_OFFSET = {
  top: 20,
  left: 20,
  right: 20,
  bottom: 30
};

/**
 * Do not use unless you have a very good reason. All the elements that
 * respond to press should have a visual feedback when touched. This is
 * one of the primary reason a "web" app doesn't feel "native".
 */
class TouchableWithoutFeedback extends Component {


  static propTypes = {
    /**
     * Called when the touch is released, but not if cancelled (e.g. by a scroll
     * that steals the responder lock).
     */
    onPress: React.PropTypes.func,
    onPressIn: React.PropTypes.func,
    onPressOut: React.PropTypes.func,

    onLongPress: React.PropTypes.func,

    /**
     * Delay in ms, from the start of the touch, before onPressIn is called.
     */
    delayPressIn: React.PropTypes.number,
    /**
     * Delay in ms, from the release of the touch, before onPressOut is called.
     */
    delayPressOut: React.PropTypes.number,
    /**
     * Delay in ms, from onPressIn, before onLongPress is called.
     */
    delayLongPress: React.PropTypes.number,
  }

  state = this.touchableGetInitialState()

  // componentDidMount: function() {
  // ensurePositiveDelayProps(this.props);
  // },

  componentWillReceiveProps(nextProps) {
    // ensurePositiveDelayProps(nextProps);
  }

  /**
   * `Touchable.Mixin` self callbacks. The mixin will invoke these if they are
   * defined on your component.
   */
  touchableHandlePress(e) {
    var touchBank = e.touchHistory.touchBank[e.touchHistory.indexOfSingleActiveTouch];
    if (touchBank) {
      var offset = Math.sqrt(Math.pow(touchBank.startPageX - touchBank.currentPageX, 2)
          + Math.pow(touchBank.startPageY - touchBank.currentPageY, 2));
      var velocity = (offset / (touchBank.currentTimeStamp - touchBank.startTimeStamp)) * 1000;
      if (velocity < 100) this.props.onPress && this.props.onPress(e);
    } else {
      this.props.onPress && this.props.onPress(e);
    }
  }

  touchableHandleActivePressIn(e) {
    this.props.onPressIn && this.props.onPressIn(e);
  }

  touchableHandleActivePressOut(e) {
    this.props.onPressOut && this.props.onPressOut(e);
  }

  touchableHandleLongPress(e) {
    this.props.onLongPress && this.props.onLongPress(e);
  }

  touchableGetPressRectOffset(): typeof PRESS_RECT_OFFSET {
    return PRESS_RECT_OFFSET; // Always make sure to predeclare a constant!
  }

  touchableGetHighlightDelayMS() {
    return this.props.delayPressIn || 0;
  }

  touchableGetLongPressDelayMS() {
    return this.props.delayLongPress === 0 ? 0 :
    this.props.delayLongPress || 500;
  }

  touchableGetPressOutDelayMS() {
    return this.props.delayPressOut || 0;
  }

  render() {
    // Note(avik): remove dynamic typecast once Flow has been upgraded
    return React.cloneElement(React.Children.only(this.props.children), {
      onStartShouldSetResponder: this.touchableHandleStartShouldSetResponder.bind(this),
      onResponderTerminationRequest: this.touchableHandleResponderTerminationRequest.bind(this),
      onResponderGrant: this.touchableHandleResponderGrant.bind(this),
      onResponderMove: this.touchableHandleResponderMove.bind(this),
      onResponderRelease: this.touchableHandleResponderRelease.bind(this),
      onResponderTerminate: this.touchableHandleResponderTerminate.bind(this)
    });
  }

};

mixin.onClass(TouchableWithoutFeedback, Touchable.Mixin);

module.exports = TouchableWithoutFeedback;