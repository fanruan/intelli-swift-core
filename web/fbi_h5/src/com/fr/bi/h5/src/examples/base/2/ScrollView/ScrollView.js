var React = require('lib');
var FakeObjectDataListStore = require('../../helpers/FakeObjectDataListStore');
var {
    Component,
    ScrollView,
    StyleSheet,
    Text,
    View
} = React;

class ScrollViewDemo extends Component {

    render() {
        const data = new FakeObjectDataListStore(1000);
        var rows1 = [], rows2 = [], rows3=[];

        for (var i = 0; i < 1000; i++) {
            rows1.push(<Text>
                {data.getObjectAt(i)['firstName']}
            </Text>)
            rows2.push(<Text>
                {data.getObjectAt(i)['firstName']}
            </Text>)
            rows3.push(<Text>
                {data.getObjectAt(i)['firstName']}
            </Text>)
        }
        return (
            <View>
                <ScrollView
                    onScroll={()=>{console.log('onScroll!'); }}
                    // onTouchStart={()=>{console.log('onTouchStart!'); }}
                    // onTouchMove={()=>{console.log('onTouchMove!'); }}
                    // onTouchEnd={()=>{console.log('onTouchEnd!'); }}
                    onResponderRelease={()=>{console.log('onResponderRelease!'); }}
                    onScrollResponderKeyboardDismissed={()=>{console.log('onScrollResponderKeyboardDismissed!'); }}
                    onResponderGrant={()=>{console.log('onResponderGrant!'); }}
                    onScrollBeginDrag={()=>{console.log('onScrollBeginDrag!'); }}
                    onScrollEndDrag={()=>{console.log('onScrollEndDrag!'); }}
                    onMomentumScrollBegin={()=>{console.log('onMomentumScrollBegin!'); }}
                    onMomentumScrollEnd={()=>{console.log('onMomentumScrollEnd!'); }}

                    onKeyboardWillShow={()=>{console.log('onKeyboardWillShow!'); }}
                    onKeyboardWillHide={()=>{console.log('onKeyboardWillHide!'); }}
                    onKeyboardDidShow={()=>{console.log('onKeyboardDidShow!'); }}
                    onKeyboardDidHide={()=>{console.log('onKeyboardDidHide!'); }}

                    scrollEventThrottle = {1}
                    style={styles.scrollView}>
                    {rows1}
                </ScrollView>

                <ScrollView
                    horizontal={true}
                    onScroll={()=>{console.log('onScroll!');}}
                    style={[styles.scrollView, styles.horizontalScrollView]}>
                    {rows2}
                </ScrollView>
                <View style={styles.view} onScroll={()=>{console.log('onScroll!');}}>{rows3}</View>
            </View>
        )
    }
}

var styles = StyleSheet.create({
    scrollView: {
        height: 300
    },
    view: {
        overflow: 'scroll',
        height: 300
    },
    horizontalScrollView: {
        backgroundColor: '#6A85B1',
        height: 120
    }
});

export default ScrollViewDemo
