/**
 * Copyright (c) 2015-present, Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * Copyright (c) 2015, Facebook, Inc.  All rights reserved.
 *
 * @providesModule ReactScrollView
 */
'use strict';

import React, { PropTypes, Component} from 'react';
import ReactDOM from 'react-dom';
import ScrollResponder from '../ListView/ScrollResponder.web';
import StyleSheet from '../StyleSheet/StyleSheet.web';
import View from '../View/View.web';
import throttle from 'domkit/throttle';
import mixin from 'react-mixin';
// import autobind from 'autobind-decorator';

const SCROLLVIEW = 'ScrollView';
const INNERVIEW = 'InnerScrollView';
const CONTENT_EXT_STYLE = ['padding', 'paddingTop', 'paddingBottom', 'paddingLeft', 'paddingRight'];

/**
 * Component that wraps platform ScrollView while providing
 * integration with touch locking "responder" system.
 *
 * Keep in mind that ScrollViews must have a bounded height in order to work,
 * since they contain unbounded-height children into a bounded container (via
 * a scroll interaction). In order to bound the height of a ScrollView, either
 * set the height of the view directly (discouraged) or make sure all parent
 * views have bounded height. Forgetting to transfer `{flex: 1}` down the
 * view stack can lead to errors here, which the element inspector makes
 * easy to debug.
 *
 * Doesn't yet support other contained responders from blocking this scroll
 * view from becoming the responder.
 */
class ScrollView extends Component {

  state = this.scrollResponderMixinGetInitialState();

  /**
   * Returns a reference to the underlying scroll responder, which supports
   * operations like `scrollTo`. All ScrollView-like components should
   * implement this method so that they can be composed while providing access
   * to the underlying scroll responder's methods.
   */
  getScrollResponder() {
    return this;
  }

  getInnerViewNode() {
    return this.refs[INNERVIEW];
  }

  scrollTo(destY: number, destX: number) {
    // $FlowFixMe - Don't know how to pass Mixin correctly. Postpone for now
    // this.getScrollResponder().scrollResponderScrollTo(destX || 0, destY || 0);
    this.scrollWithoutAnimationTo(destY, destX);
  }

  scrollWithoutAnimationTo(destY: number, destX: number) {
    // $FlowFixMe - Don't know how to pass Mixin correctly. Postpone for now
    // this.getScrollResponder().scrollResponderScrollWithouthAnimationTo(
    //   destX || 0,
    //   destY || 0,
    // );

    let scrollView = ReactDOM.findDOMNode(this.refs[SCROLLVIEW]);
    scrollView.scrollTop = destY || 0;
    scrollView.scrollLeft = destX || 0;
  }

  handleScroll(e: Event) {
    // if (__DEV__) {
    //   if (this.props.onScroll && !this.props.scrollEventThrottle) {
    //     console.log(
    //       'You specified `onScroll` on a <ScrollView> but not ' +
    //       '`scrollEventThrottle`. You will only receive one event. ' +
    //       'Using `16` you get all the events but be aware that it may ' +
    //       'cause frame drops, use a bigger number if you don\'t need as ' +
    //       'much precision.'
    //     );
    //   }
    // }
    // if (Platform.OS === 'android') {
    //   if (this.props.keyboardDismissMode === 'on-drag') {
    //     dismissKeyboard();
    //   }
    // }

    this.props.onScroll && this.props.onScroll(e);
  }

  render() {
    let {
      style,
      ...otherProps
    } = this.props;

    let contentContainerExtStyle = {};

    if (style) {
      for (let i = 0; i < CONTENT_EXT_STYLE.length; i++) {
        if (typeof style[CONTENT_EXT_STYLE[i]] === 'number') {
          contentContainerExtStyle[CONTENT_EXT_STYLE[i]] = style[CONTENT_EXT_STYLE[i]];
        }
      }
    }

    let contentContainerStyle = [
      styles.contentContainer,
      this.props.horizontal && styles.contentContainerHorizontal,
      this.props.contentContainerStyle,
      contentContainerExtStyle
    ];
    // if (__DEV__ && this.props.style) {
    //   let style = flattenStyle(this.props.style);
    //   let childLayoutProps = ['alignItems', 'justifyContent']
    //     .filter((prop) => style && style[prop] !== undefined);
    //   invariant(
    //     childLayoutProps.length === 0,
    //     'ScrollView child layout (' + JSON.stringify(childLayoutProps) +
    //       ') must by applied through the contentContainerStyle prop.'
    //   );
    // }

    let contentContainer =
      <View
        ref={INNERVIEW}
        style={contentContainerStyle}
        removeClippedSubviews={this.props.removeClippedSubviews}
        collapsable={false}>
        {this.props.children}
      </View>;

    let alwaysBounceHorizontal =
      this.props.alwaysBounceHorizontal !== undefined ?
        this.props.alwaysBounceHorizontal :
        this.props.horizontal;

    let alwaysBounceVertical =
      this.props.alwaysBounceVertical !== undefined ?
        this.props.alwaysBounceVertical :
        !this.props.horizontal;

    let handleScroll = () => {};
    if (this.props.scrollEventThrottle && this.props.onScroll) {
      handleScroll = throttle(this.handleScroll.bind(this), this.props.scrollEventThrottle);
    }

    let props = {
      ...otherProps,
      alwaysBounceHorizontal,
      alwaysBounceVertical,
      style: ([styles.base, this.props.style]),
      onTouchStart: this.scrollResponderHandleTouchStart.bind(this),
      onTouchMove: this.scrollResponderHandleTouchMove.bind(this),
      onTouchEnd: this.scrollResponderHandleTouchEnd.bind(this),
      onScrollBeginDrag: this.scrollResponderHandleScrollBeginDrag.bind(this),
      onScrollEndDrag: this.scrollResponderHandleScrollEndDrag.bind(this),
      onMomentumScrollBegin: this.scrollResponderHandleMomentumScrollBegin.bind(this),
      onMomentumScrollEnd: this.scrollResponderHandleMomentumScrollEnd.bind(this),
      onStartShouldSetResponder: this.scrollResponderHandleStartShouldSetResponder.bind(this),
      onStartShouldSetResponderCapture: this.scrollResponderHandleStartShouldSetResponderCapture.bind(this),
      // onScrollShouldSetResponder: this.scrollResponderHandleScrollShouldSetResponder,
      // onScroll: handleScroll,
      onScrollShouldSetResponder: handleScroll.bind(this),
      // replace onScroll in the props
      onScroll: () => {},
      onResponderGrant: this.scrollResponderHandleResponderGrant.bind(this),
      onResponderTerminationRequest: this.scrollResponderHandleTerminationRequest.bind(this),
      //onResponderTerminate: this.scrollResponderHandleTerminate.bind(this),
      onResponderRelease: this.scrollResponderHandleResponderRelease.bind(this),
      onResponderReject: this.scrollResponderHandleResponderReject.bind(this)
    };

    return (
      <View {...props} ref={SCROLLVIEW}>
        {contentContainer}
      </View>
    );
  }
}

let styles = StyleSheet.create({
  base: {
    overflow: 'scroll',
    WebkitOverflowScrolling: 'touch',
    flex: 1
  },
  contentContainer: {
    position: 'absolute',
    minWidth: '100%'
  },
  contentContainerHorizontal: {
    alignSelf: 'flex-start',
    flexDirection: 'row'
  }
});

mixin.onClass(ScrollView, ScrollResponder.Mixin);
//autobind(ScrollView);

ScrollView.isReactNativeComponent = true;

export default ScrollView;
