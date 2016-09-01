/**
 * Copyright (c) 2015-present, Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * @providesModule ReactAnimated
 * @flow
 */
'use strict';

import Animated from 'animated';
import CSSPropertyOperations from 'react/lib/CSSPropertyOperations';

import flattenStyle from '../StyleSheet/FlattenStyle.web.js';
import Image from '../Image/Image.web';
import Text from '../Text/Text.web';
import View from '../View/View.web';

// { scale: 2 } => 'scale(2)'
function mapTransform(t) {
  var k = Object.keys(t)[0];
  return `${k}(${t[k]})`;
}

// NOTE(lmr):
// Since this is a hot code path, right now this is mutative...
// As far as I can tell, this shouldn't cause any unexpected behavior.
function mapStyle(style) {
  if (style && style.transform && typeof style.transform !== 'string') {
    // TODO(lmr): this doesn't attempt to use vendor prefixed styles
    style.transform = style.transform.map(mapTransform).join(' ');
  }
  return style;
}

function ApplyAnimatedValues(instance, props) {
  if (instance.setNativeProps) {
    instance.setNativeProps(props);
  } else if (instance.nodeType && instance.setAttribute !== undefined) {
    CSSPropertyOperations.setValueForStyles(instance, mapStyle(props.style));
  } else {
    return false;
  }
}

/* eslint-disable */
Animated.inject.ApplyAnimatedValues(ApplyAnimatedValues);
Animated.inject.FlattenStyle(flattenStyle);
/* eslint-enable */

export default {
  ...Animated,
  View: Animated.createAnimatedComponent(View),
  Text: Animated.createAnimatedComponent(Text),
  Image: Animated.createAnimatedComponent(Image)
};
