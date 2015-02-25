package com.mbui.sdk.feature.abs;

import android.support.annotation.NonNull;

/**
 * 需要实现Feature容器的基类，实现这个借口
 * Created by chenwei on 15/1/14.
 */
public interface AbsFeatureBuilder<T extends AbsFeature> {
    public void addFeature(@NonNull T feature);

    public void removeFeature(@NonNull T feature);
}
