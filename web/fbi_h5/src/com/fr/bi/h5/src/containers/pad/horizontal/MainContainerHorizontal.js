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

import {Colors, Sizes, TemplateFactory} from 'data'
import {Layout} from 'layout'

import LayoutContainer from './Layout/LayoutContainer'

class MainContainerHorizontal extends Component {
    static contextTypes = {
        actions: React.PropTypes.object,
        $template: React.PropTypes.object
    };

    static propTypes = {};

    constructor(props, context) {
        super(props, context);
    }

    navigationBarRouteMapper() {
        const self = this;
        return {

            LeftButton (route, navigator, index, navState) {
                return null;
            },

            RightButton (route, navigator, index, navState) {
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
        const {name} = route;
        if (name === 'index') {
            return <LayoutContainer {...props} width={props.width} height={props.height - 50}/>;
        }
    }

    render() {
        const initialRoute = {name: 'index', title: '首页'};
        this.template = TemplateFactory.createTemplate(this.props.$template);
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
mixin.onClass(MainContainerHorizontal, ReactComponentWithImmutableRenderMixin);
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

export default MainContainerHorizontal
