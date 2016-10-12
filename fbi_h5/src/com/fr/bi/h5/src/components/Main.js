import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
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
    TouchableOpacity,
    TouchableHighlight
} from 'lib'

import {Colors, Size, Template} from 'data'
import {Layout} from 'layout'

import Toolbar from './Toolbar'
import LayoutComponent from './Layout/LayoutComponent'

const {width, height} = Dimensions.get('window');

class Main extends Component {
    static contextTypes = {
        actions: React.PropTypes.object,
        $template: React.PropTypes.object
    };

    static propTypes = {};

    constructor(props, context) {
        super(props, context);
        console.log(props);
        this.template = new Template(props.$template);
    }

    navigationBarRouteMapper() {
        const self = this;
        return {

            LeftButton (route, navigator, index, navState) {
                if (index === 0) {
                    return null;
                }

                return (
                    <TouchableHighlight
                        onPress={() => navigator.pop()}
                        underlayColor={Colors.PRESS}
                        style={styles.navBarLeftButton}>
                        <Text style={[styles.navBarText, styles.navBarButtonText]}>
                            返回
                        </Text>
                    </TouchableHighlight>
                );
            },

            RightButton (route, navigator, index, navState) {
                if (index === 0) {
                    return null;
                }

                if (route.name === 'widget') {
                    return (
                        <TouchableHighlight
                            onPress={() => {
                                const prevRoute = navState.routeStack[navState.presentedIndex - 1];
                                if (route.$template) {
                                    prevRoute.$template = route.$template;
                                    navigator.replacePreviousAndPop(prevRoute);
                                } else {
                                    navigator.pop();
                                }
                            }}
                            underlayColor={Colors.PRESS}
                            style={styles.navBarRightButton}>
                            <Text style={[styles.navBarText, styles.navBarButtonText]}>
                                确定
                            </Text>
                        </TouchableHighlight>
                    );
                }

                if (route.name === 'list') {
                    return (
                        <TouchableHighlight
                            onPress={() => {
                                const prevRoute = navState.routeStack[navState.presentedIndex - 1];
                                if (route.$template) {
                                    prevRoute.$template = route.$template;
                                    self.context.actions.updateTemplate(route.$template);
                                    navigator.replacePreviousAndPop(prevRoute);
                                } else {
                                    navigator.pop();
                                }
                            }}
                            underlayColor={Colors.PRESS}
                            style={styles.navBarRightButton}>
                            <Text style={[styles.navBarText, styles.navBarButtonText]}>
                                查询
                            </Text>
                        </TouchableHighlight>
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
        }
    };

    renderScene(route, navigationOperations, onComponentRef) {
        const {...props} = this.props;
        const {name, Component, title, onValueChange, ...others} = route;
        if (name === 'index') {
            if (this.template.hasControlWidget()) {
                return <Layout style={{height: '100%'}} dir='top' box='last'>
                    <LayoutComponent width={width} height={height - 50 - Size.ITEM_HEIGHT} {...props}
                                     navigator={navigationOperations}/>

                    <Toolbar {...props} navigator={navigationOperations}>

                    </Toolbar>
                </Layout>
            }
            return <LayoutComponent width={width} height={height} {...props}/>;
        }
        return (
            <Component
                width={width} height={height - 50}
                {...others}
                onValueChange={$template=> {
                    route.$template = $template;
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
                        routeMapper={this.navigationBarRouteMapper()}
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
        paddingTop: 50
    },
    sceneStyle: {
        backgroundColor: Colors.TEXT
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
