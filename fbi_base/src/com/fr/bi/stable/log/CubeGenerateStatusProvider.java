package com.fr.bi.stable.log;

/**
 * Created by 小灰灰 on 2014/10/21.
 */
public interface CubeGenerateStatusProvider {
    public int getPercent();

    public void setPercent(int percent);

    public String getDetail();

    public void setDetail(String s);
}