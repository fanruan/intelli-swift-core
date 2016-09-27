import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import * as TodoActions from '../actions/todos';
import {requestAnimationFrame} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch,
    Navigator,
    TouchableOpacity
} from 'lib'

import {Colors} from 'data'

import Toolbar from './Toolbar'
import Layout from './Layout'

const {width, height} = Dimensions.get('window');

const NavigationBarRouteMapper = {

    LeftButton (route, navigator, index, navState) {
        if (index === 0) {
            return null;
        }

        return (
            <TouchableOpacity
                onPress={() => navigator.pop()}
                style={styles.navBarLeftButton}>
                <Text style={[styles.navBarText, styles.navBarButtonText]}>
                    返回
                </Text>
            </TouchableOpacity>
        );
    },

    RightButton (route, navigator, index, navState) {
        return (
            <View/>
        );
    },

    Title (route, navigator, index, navState) {
        return (
            <Text style={[styles.navBarText, styles.navBarTitleText]}>
                {route.title}
            </Text>
        );
    }

};

class Main extends Component {
    static propTypes = {};

    constructor(props, context) {
        super(props, context);
    }

    renderScene(route, navigationOperations, onComponentRef) {
        const {...props} = this.props;
        if (route.name === 'index') {
            if (props.template.hasControlWidget()) {
                return <View style={styles.index}>
                    <Layout width={width} height={height - 94} {...props} navigator={navigationOperations}/>

                    <Toolbar {...props} navigator={navigationOperations}>

                    </Toolbar>
                </View>
            }
            return <Layout width={width} height={height} {...props}/>;
        }
        return (
            <route.Component
                width={width} height={height - 50}
                {...props}
                navigator={navigationOperations}
            />
        );
    }

    render() {
        const initialRoute = {name: 'index', title: '首页'};
        return (
            <Navigator
                style={styles.wrapper}
                initialRoute={initialRoute}
                renderScene={this.renderScene.bind(this)}
                navigationBar={
                    <Navigator.NavigationBar
                        routeMapper={NavigationBarRouteMapper}
                        style={styles.navBar}
                    />
                }
                configureScene={(route) => {
                    if (route.sceneConfig) {
                        return route.sceneConfig;
                    }
                    return Navigator.SceneConfigs.FloatFromRight;
                }}
            />
        );
    }
}
mixin.onClass(Main, PureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        flex: 1,
        backgroundColor: '#fff',
        paddingTop: 50
    },
    index: {
        flex: 1
    },
    navBar: {
        backgroundColor: '#efeff4',
        height: 50,
        borderBottomWidth: 1,
        borderBottomColor: Colors.BORDER
    },
    navBarText: {
        fontSize: 16
    },
    navBarTitleText: {
        color: '#000',
        fontWeight: 700
    },
    navBarLeftButton: {
        color: Colors.HIGHLIGHT,
        paddingLeft: 10
    }
});
// export default Main

function mapStateToProps(state) {
    /* Populated by react-webpack-redux:reducer */
    const props = {
        template: state.template
    };
    return props;
}
function mapDispatchToProps(dispatch) {
    /* Populated by react-webpack-redux:action */
    const actionMap = {actions: bindActionCreators(TodoActions, dispatch)};
    return actionMap;
}
export default connect(mapStateToProps, mapDispatchToProps)(Main);
