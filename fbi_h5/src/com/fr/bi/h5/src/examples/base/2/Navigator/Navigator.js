import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch,
    Navigator,
    TouchableHighlight
} from 'lib'

import {Grid, ScrollSync, AutoSizer} from 'base'


class NavigatorDemo extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {};

    state = {};

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps() {

    }

    componentWillUpdate() {

    }

    render() {
        const {...props} = this.props;
        return <Navigator
            style= {styles.container}
            initialRoute= {{
                component: HomeScene,
                name: 'home'
            }}
            configureScene={() => {
                return Navigator.SceneConfigs.HorizontalSwipeJump;
            }}
            renderScene={(route, navigator) => {
                let Component = route.component;
                if(route.component) {
                    return <Component {...route.params} navigator={navigator} />
                }
            }} >

        </Navigator>
    }

}
mixin.onClass(NavigatorDemo, PureRenderMixin);
const style = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
/*--  首页页面组件 --*/
var HomeScene = React.createClass({
    getInitialState:function () {
        return {
            id: 'AXIBA001',
            flag: null
        };
    },
    render: function() {
        return (
            <View style={styles.home}>
                <TouchableHighlight onPress={this.onPress}>
                    <Text>push me!{this.state.flag && ' I \'m ' + this.state.flag + ', i come from second page'}</Text>
                </TouchableHighlight>
            </View>
        );
    },
    onPress: function() {
        var _me = this;
        //或者写成 const navigator = this.props.navigator;
        const { navigator } = this.props;
        if(navigator)
        {
            navigator.push({
                name: 'touch View',
                component: SecondScene,
                params: {
                    id: this.state.id,
                    getSomething:function(flag) {
                        _me.setState({
                            flag: flag
                        });
                    }
                }
            })
        }
    }
});
/*--  push后的页面组件 --*/
var SecondScene = React.createClass({
    render: function() {
        return (
            <View style={styles.home}>
                <TouchableHighlight onPress={this.onPress}>
                    <Text>push sucess!I get {this.props.id},i want back!</Text>
                </TouchableHighlight>
            </View>
        );
    },
    onPress: function() {
        const { navigator } = this.props;

        if(this.props.getSomething) {
            var flag = 'Axiba002'
            this.props.getSomething(flag);
        }
        if(navigator) {
            navigator.pop();
        }
    }
});

/*布局样式*/
var styles = StyleSheet.create({
    container: {
        flex: 1,
        // justifyContent: 'center',
        // alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    home: {
        paddingTop:74,

    },
});


export default NavigatorDemo
