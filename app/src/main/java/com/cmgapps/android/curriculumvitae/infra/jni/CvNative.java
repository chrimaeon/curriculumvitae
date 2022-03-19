/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.curriculumvitae.infra.jni;

import android.content.Context;

import androidx.annotation.NonNull;

@SuppressWarnings("JavaJniMissingFunction")
public class CvNative {

    static {
        System.loadLibrary("CvNative");
    }

    public static boolean checkSignature(@NonNull Context context) {
        return cS(context.getApplicationContext());
    }

    private static native boolean cS(@NonNull Context appContext);
}
