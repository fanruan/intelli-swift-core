package com.fr.bi.base;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.BICoreService;
import com.fr.bi.exception.BIAmountLimitUnmetException;

import javax.activation.UnsupportedDataTypeException;
import java.util.List;


/**
 * Created by Connery on 2016/1/25.
 */
public class BICoreObject4GeneratorTest implements BICoreService {
    @BICoreField
    private String attri;
    @BICoreField
    private String attrit_1;
    @BICoreField
    private String attrit_2;
    @BICoreField
    private List<String> list;

    private List<String> list_2;

    public BICoreObject4GeneratorTest(String attri, String attrit_1, String attrit_2) {
        this.attri = attri;
        this.attrit_1 = attrit_1;
        this.attrit_2 = attrit_2;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public BICore fetchObjectCore() {
        BICoreGenerator generator = new BICoreGenerator(this);
        try {
            generator.addAdditionalAttribute(list_2);
        } catch (UnsupportedDataTypeException e) {

        } catch (BIAmountLimitUnmetException ex) {

        }
        return generator.fetchObjectCore();
    }
}