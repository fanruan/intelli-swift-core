var React = require('lib');
var FakeObjectDataListStore = require('../../helpers/FakeObjectDataListStore');
var {
    Component,
    StyleSheet,
    PanResponder,
    Text,
    View
} = React;

class ViewDemo extends Component {

    render() {
        const data = new FakeObjectDataListStore(1000);
        var rows = [];

        for (var i = 0; i < 1000; i++) {
            rows.push(<Text>
                {data.getObjectAt(i)['firstName']}
            </Text>)
        }

        this._panResponder = PanResponder.create({
            onMoveShouldSetPanResponder: (...args)=> {
                console.log('onMoveShouldSetResponder');
                console.log(args);
                return true;
            },
            onPanResponderGrant: (...args)=> {
                console.log('onPanResponderGrant')
                console.log(args);
            },
            onPanResponderMove: (...args)=> {
                console.log('onPanResponderMove');
                console.log(args);
            },
            onPanResponderTerminationRequest: (...args)=> {
                console.log('onPanResponderTerminationRequest');
                console.log(args);
            },
            onPanResponderRelease: (...args)=> {
                console.log('onPanResponderRelease');
                console.log(args);
            },
            onPanResponderTerminate: (...args)=> {
                console.log('onPanResponderTerminate');
                console.log(args);
            }
        });
        return (
            <View style={styles.view}
                {...this._panResponder.panHandlers}

            >{rows}</View>
        )
    }
}

var styles = StyleSheet.create({
    view: {
        overflow: 'scroll',
        height: 300
    }
});

export default ViewDemo
