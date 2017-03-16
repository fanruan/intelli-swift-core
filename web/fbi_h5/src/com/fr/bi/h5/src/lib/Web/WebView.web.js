/**
 * web component
 * Created by Young's on 2016/10/24.
 */
'use strict';

import React, {Component} from 'react';
import {Mixin as LayoutMixin} from '../Utilties/LayoutMixin';
import {Mixin as NativeMethodsMixin} from '../Utilties/NativeMethodsMixin.web';
import mixin from 'react-mixin';

class WebView extends Component {

    static defaultProps = {
        width: '100%',
        height: '100%',
        src: ""
    };

    render() {
        let props = {...this.props};

        return (
            <iframe {...props}></iframe>
        )
    }
}

mixin.onClass(WebView, LayoutMixin);
mixin.onClass(WebView, NativeMethodsMixin);

WebView.isReactNativeComponent = true;

export default WebView;