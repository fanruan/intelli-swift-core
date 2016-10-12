/**
 * Created by Young's on 2016/10/12.
 */
export default WidgetFactory = {
    createWidget: ($widget)=> {
        const wType = $widget.get('type');
        switch (wType) {
            case BICst.WIDGET.TABLE:
                return new TableWidget($widget);
        }
    }
    
};