/**
 * Copyright (c) 2015-present, Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * @providesModule ReactWeb
 */
'use strict';
import React from 'react';
import {extendCreateElement} from './StyleSheet/StyleSheet.web';

// proxy origin react createElement
extendCreateElement(React);

// React
export * from 'react';

// Components
export ActivityIndicatorIOS from './ActivityIndicator/ActivityIndicator.web';
// export DatePickerIOS from './DatePickerIOS/DatePickerIOS.web';
export DrawerLayoutAndroid from './DrawerLayout/DrawerLayout.web';
export Image from './Image/Image.web';
export ListView from './ListView/ListView.web';
export Modal from './Modal/Modal.web';
export Navigator from './Navigator/Navigator.web';
export Picker from './Picker/Picker.web';
export DatePickerIOS from './DatePickerIOS/DatePickerIOS';
export ProgressViewIOS from './ProgressView/ProgressView.web';
export ScrollView from './ScrollView/ScrollView.web';
export SegmentedControlIOS from './SegmentedControl/SegmentedControl.web';
export SliderIOS from './Slider/Slider.web';
export Switch from './Switch/Switch.web';
export SwitchAndroid from './Switch/Switch.web';
export SwitchIOS from './Switch/Switch.web';
export TabBarIOS from './TabBar/TabBar.web';
export Text from './Text/Text.web';
export TextInput from './TextInput/TextInput.web';
export ToastAndroid from './Toast/Toast.web';
export Toast from './Toast/Toast.web';
export Touchable from './Touchable/Touchable';
export TouchableHighlight from './Touchable/TouchableHighlight.web';
export TouchableOpacity from './Touchable/TouchableOpacity.web';
export TouchableWithoutFeedback from './Touchable/TouchableWithoutFeedback.web';
export TouchableBounce from './Touchable/TouchableBounce.web';
export RefreshControl from './RefreshControl/RefreshControl.web';
export View from './View/View.web';
export ViewPagerAndroid from './ViewPager/ViewPager.web';
export ViewPager from './ViewPager/ViewPager.web';


// APIs
export Alert from './Alert/Alert.web';
export AlertIOS from './Alert/Alert.web';
export Animated from './Animated/Animated.web';
export AsyncStorage from './Storage/AsyncStorage.web';
export Dimensions from './Dimensions/Dimensions.web';
export Easing from 'animated/lib/Easing';
export InteractionManager from './Interaction/InteractionManager.web';
export PanResponder from './PanResponder/PanResponder.web';
export PixelRatio from './PixelRatio/PixelRatio.web';
export StyleSheet from './StyleSheet/StyleSheet.web';
export Jsonp from './Fetch/Jsonp.web';
export Fetch from './Fetch/Fetch.web';

// Plugins
export NativeModules from './NativeModules/NativeModules.web';
export Platform from './Platform/Platform.web';
export processColor from './StyleSheet/processColor.web';

// React
// export {
//   // Components
//   ActivityIndicatorIOS,
// // export DatePickerIOS,
//   DrawerLayoutAndroid,
//   Image,
//   ListView,
//   Modal,
//   Navigator,
//   PickerIOS,
//   Picker,
//   ProgressViewIOS,
//   ScrollView,
//   SegmentedControlIOS,
//   SliderIOS,
//   Switch,
//   SwitchAndroid,
//   SwitchIOS,
//   TabBarIOS,
//   Text,
//   TextInput,
//   ToastAndroid,
//   Toast,
//   Touchable,
//   TouchableHighlight,
//   TouchableOpacity,
//   TouchableWithoutFeedback,
//   TouchableBounce,
//   RefreshControl,
//   View,
//   ViewPagerAndroid,
//   ViewPager,
//
//
// // APIs
//   Alert,
//   AlertIOS,
//   Animated,
//   AsyncStorage,
//   Dimensions,
//   Easing,
//   InteractionManager,
//   PanResponder,
//   PixelRatio,
//   StyleSheet,
//   Jsonp,
//   Fetch,
//
// // Plugins
//   NativeModules,
//   Platform,
//   processColor
// };


// Match the react-native export signature, which uses CommonJS
// (not ES6), where this works:
//    import ReactNative, {View} from 'react-native';
//    ReactNative.View === View
export default module.exports;
