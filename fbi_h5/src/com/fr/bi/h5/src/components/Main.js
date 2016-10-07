import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import * as TodoActions from '../actions/todos';
import {requestAnimationFrame, ReactComponentWithImmutableRenderMixin} from 'core'
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

import {Colors, Template} from 'data'

import Toolbar from './Toolbar'
import Layout from './Layout/Layout'

const {width, height} = Dimensions.get('window');

class Main extends Component {
    static propTypes = {};

    constructor(props, context) {
        super(props, context);
        console.log(props);
        this.template = new Template(props.$$template);
        // template={new Template(template)} actions={actions}
    }

    NavigationBarRouteMapper = {

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
            if (index === 0) {
                return null;
            }

            if (route.name === 'widget') {
                return (
                    <TouchableOpacity
                        onPress={() => {
                            console.log(route);
                            if (route.value) {
                                navigator.pop();
                            }
                        }}
                        style={styles.navBarRightButton}>
                        <Text style={[styles.navBarText, styles.navBarButtonText]}>
                            确定
                        </Text>
                    </TouchableOpacity>
                );
            }

            if (route.name === 'list') {
                return (
                    <TouchableOpacity
                        onPress={() => {
                            console.log(route);
                            navigator.pop();
                        }}
                        style={styles.navBarRightButton}>
                        <Text style={[styles.navBarText, styles.navBarButtonText]}>
                            查询
                        </Text>
                    </TouchableOpacity>
                );
            }

            return null;
        },

        Title (route, navigator, index, navState) {
            return (
                <Text style={[styles.navBarText, styles.navBarTitleText]}>
                    {route.title}
                </Text>
            );
        }
    };

    renderScene(route, navigationOperations, onComponentRef) {
        const {...props} = this.props;
        const {onValueChange, ...others} = route;
        if (route.name === 'index') {
            if (this.template.hasControlWidget()) {
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
                {...others}
                onValueChange={value=> {
                    onValueChange && onValueChange(value);
                    route.value = value;
                }}
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
                        routeMapper={this.NavigationBarRouteMapper}
                        style={styles.navBar}
                    />
                }
                configureScene={(route) => {
                    if (route.sceneConfig) {
                        return route.sceneConfig;
                    }
                    return Navigator.SceneConfigs.FloatFromRight;
                }}
                sceneStyle={styles.sceneStyle}
            />
        );
    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps(nextProps) {
        debugger
    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }
}
mixin.onClass(Main, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        flex: 1,
        paddingTop: 50
    },
    index: {
        flex: 1
    },
    sceneStyle: {
        backgroundColor: '#ffffff'
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
    },
    navBarRightButton: {
        color: Colors.HIGHLIGHT,
        paddingRight: 10
    }
});

export default Main
