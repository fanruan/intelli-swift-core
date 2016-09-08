'use strict';

import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux';
import configureStore from './stores';
import App from './containers/App';
import StyleSheet from './lib/StyleSheet/StyleSheet.web';
import View from './lib/View/View.web';
import Portal from './lib/Portal/Portal';

import 'reset.css';
import 'css/utils/common.css';
import 'css/utils/font.css';
import 'css/utils/icon.css';
import 'css/utils/background.css';
import 'css/utils/base.css';

class AppContainer extends Component {

    render() {
        return (
            <View
                ref="main"
                className={StyleSheet.rootClassName}
                style={styles.appContainer}>
                <App
                    {...this.props}/>
                <Portal />
            </View>
        );
    }
}

var styles = StyleSheet.create({
    // This is needed so the application covers the whole screen
    // and therefore the contents of the React are not clipped.
    appContainer: {
        position: 'absolute',
        left: 0,
        top: 0,
        right: 0,
        bottom: 0
    }
});

const store = configureStore();

ReactDOM.render(
    <Provider store={store}>
        <AppContainer/>
    </Provider>,
    document.getElementById('app')
);
