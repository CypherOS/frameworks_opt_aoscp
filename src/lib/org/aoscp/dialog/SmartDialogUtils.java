/*
 * Copyright (C) 2017 CypherOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aoscp.dialog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.TypedValue;

public class SmartDialogUtils {

    static int dpToPixels(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    static Drawable createButtonBackgroundDrawable(@NonNull Context context, int fillColor) {
        int buttonCornerRadius = dpToPixels(context, 2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue v = new TypedValue();
            boolean hasAttribute = context.getTheme().resolveAttribute(android.R.attr.colorControlHighlight, v, true);
            int rippleColor = hasAttribute ? v.data : Color.parseColor("#88CCCCCC");
            return createButtonBackgroundDrawableLollipop(fillColor, rippleColor, buttonCornerRadius);
        }
        return createButtonBackgroundDrawableBase(fillColor, buttonCornerRadius);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable createButtonBackgroundDrawableLollipop(int fillColor, int rippleColor,int cornerRadius) {
        Drawable d = createButtonBackgroundDrawableBase(fillColor, cornerRadius);
        return new RippleDrawable(ColorStateList.valueOf(rippleColor), d, null);
    }

    private static Drawable createButtonBackgroundDrawableBase(int color, int cornerRadius) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(cornerRadius);
        d.setColor(color);
        return d;
    }

}
