import React, {
  Component,
  Text,
  View
} from 'lib';
import {
    ScrollSync
} from 'base';

class ScrollSyncDemo extends Component {

  render() {
    let a = [], b = [];
    for (let i = 0; i < 100; i++) {
      a.push(<Text>{i}</Text>);
      b.push(<Text>{i}</Text>);
    }
    return (
      <ScrollSync>
        {({clientHeight, clientWidth, scrollHeight, scrollWidth, scrollTop, scrollLeft, onScroll})=> {
          return (
            <View>
              <View style={{height: 400, overflow: 'auto'}}>
                {a}
              </View>
              <View style={{height: 400, overflow: 'auto'}}>
                {b}
              </View>
            </View>
          )
        }}
      </ScrollSync>
    )
  }
}

export default ScrollSyncDemo
