package com.fr.bi.field.target.target;

import com.fr.bi.field.target.calculator.sum.AvgCalculator;
import com.fr.bi.field.target.calculator.sum.MaxCalculator;
import com.fr.bi.field.target.calculator.sum.MinCalculator;
import com.fr.bi.field.target.calculator.sum.SumCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.report.result.TargetCalculator;

public class BINumberTarget extends BISummaryTarget {

    private static final long serialVersionUID = -3265242640054177730L;

	public SumType getSumType(){
		return getSummaryType() == BIReportConstant.SUMMARY_TYPE.AVG ? SumType.GVI : SumType.PLUS;
	}

    @Override
    public TargetCalculator createSummaryCalculator() {
    	switch(getSummaryType()){
	    	case BIReportConstant.SUMMARY_TYPE.SUM :{
	    		return  new SumCalculator(this);
	    	}
	    	case BIReportConstant.SUMMARY_TYPE.MAX :{
	    		return  new MaxCalculator(this);
	    	}
	    	case BIReportConstant.SUMMARY_TYPE.MIN :{
	    		return  new MinCalculator(this);
	    	}
	    	case BIReportConstant.SUMMARY_TYPE.AVG :{
	    		return  new AvgCalculator(this);
	    	}
	    	default : {
	    		return new SumCalculator(this);
	    	}
    	}
    }
}