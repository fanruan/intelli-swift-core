package com.fr.swift.adaptor.widget.datamining;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.WidgetDimensionBean;
import com.finebi.conf.internalimp.dashboard.widget.target.FineTargetImpl;
import com.finebi.conf.structure.dashboard.widget.FineWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.fr.swift.log.SwiftLoggers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/5/21.
 */
public class DMSwiftWidgetUtils {

    public static List<FineDimension> parseSwiftDimensions(FineWidget widget) {
        List<FineDimension> swiftDimensionList = new ArrayList<FineDimension>();
        try {
            List<FineDimension> dimensionList = widget.getDimensionList();

            for (FineDimension dimension : dimensionList) {
                if (dimension.getType() != BIDesignConstants.DESIGN.DIMENSION_TYPE.KMEANS_DIMENSION) {
                    swiftDimensionList.add(dimension);
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage(), e);
        }
        return swiftDimensionList;
    }

    public static FineTarget createFineTarget(FineTarget originalTarget, String fieldName) {
        FineTargetImpl newFineTarget = new FineTargetImpl();
        newFineTarget.setId(originalTarget.getId());
        newFineTarget.setTargetType(originalTarget.getTargetType());
        WidgetDimensionBean widgetDimensionBean = StableManager.getContext().getObject("numberWidgetDimensionBean");
        widgetDimensionBean.setName(fieldName);
        newFineTarget.setValue(widgetDimensionBean);
        newFineTarget.setIsUsed(false);
        return newFineTarget;
    }
}
