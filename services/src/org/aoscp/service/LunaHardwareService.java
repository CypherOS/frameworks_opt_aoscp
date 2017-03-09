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
package org.aoscp.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Range;

import com.android.server.SystemService;

import aoscp.app.LunaConstants;
import aoscp.hardware.ILunaHardwareService;
import aoscp.hardware.LunaHardwareManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aoscp.hardware.KeyDisabler;

/** @hide */
public class LunaHardwareService extends LunaSystemService {

    private static final boolean DEBUG = true;
    private static final String TAG = LunaHardwareService.class.getSimpleName();

    private final Context mContext;
    private final LunaHardwareInterface mLunaHwImpl;

    private interface LunaHardwareInterface {
        public int getSupportedFeatures();
        public boolean get(int feature);
        public boolean set(int feature, boolean enable);
    }

    private class LegacyLunaHardware implements LunaHardwareInterface {

        private int mSupportedFeatures = 0;

        public LegacyLunaHardware() {
            if (KeyDisabler.isSupported())
                mSupportedFeatures |= LunaHardwareManager.FEATURE_KEY_DISABLE;
        }

        public int getSupportedFeatures() {
            return mSupportedFeatures;
        }

        public boolean get(int feature) {
            switch(feature) {
                case LunaHardwareManager.FEATURE_KEY_DISABLE:
                    return KeyDisabler.isActive();
                default:
                    Log.e(TAG, "feature " + feature + " is not a boolean feature");
                    return false;
            }
        }

        public boolean set(int feature, boolean enable) {
            switch(feature) {
                case LunaHardwareManager.FEATURE_KEY_DISABLE:
                    return KeyDisabler.setActive(enable);
                default:
                    Log.e(TAG, "feature " + feature + " is not a boolean feature");
                    return false;
            }
        }

        private int[] splitStringToInt(String input, String delimiter) {
            if (input == null || delimiter == null) {
                return null;
            }
            String strArray[] = input.split(delimiter);
            try {
                int intArray[] = new int[strArray.length];
                for(int i = 0; i < strArray.length; i++) {
                    intArray[i] = Integer.parseInt(strArray[i]);
                }
                return intArray;
            } catch (NumberFormatException e) {
                /* ignore */
            }
            return null;
        }
    }

    private LunaHardwareInterface getImpl(Context context) {
        return new LegacyLunaHardware();
    }

    public LunaHardwareService(Context context) {
        super(context);
        mContext = context;
        mLunaHwImpl = getImpl(context);
        publishBinderService(LunaConstants.LUNA_HARDWARE_SERVICE, mService);
    }

    @Override
    public String getFeatureDeclaration() {
        return LunaConstants.Features.HARDWARE_ABSTRACTION;
    }

    @Override
    public void onBootPhase(int phase) {
        if (phase == PHASE_BOOT_COMPLETED) {
            Intent intent = new Intent(aoscp.content.Intent.ACTION_INITIALIZE_LUNA_HARDWARE);
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            mContext.sendBroadcastAsUser(intent, UserHandle.ALL,
                    aoscp.platform.Manifest.permission.HARDWARE_ABSTRACTION_ACCESS);
        }
    }

    private final IBinder mService = new ILunaHardwareService.Stub() {

        private boolean isSupported(int feature) {
            return (getSupportedFeatures() & feature) == feature;
        }

        @Override
        public int getSupportedFeatures() {
            mContext.enforceCallingOrSelfPermission(
                    aoscp.platform.Manifest.permission.HARDWARE_ABSTRACTION_ACCESS, null);
            return mLunaHwImpl.getSupportedFeatures();
        }

        @Override
        public boolean get(int feature) {
            mContext.enforceCallingOrSelfPermission(
                    aoscp.platform.Manifest.permission.HARDWARE_ABSTRACTION_ACCESS, null);
            if (!isSupported(feature)) {
                Log.e(TAG, "feature " + feature + " is not supported");
                return false;
            }
            return mLunaHwImpl.get(feature);
        }

        @Override
        public boolean set(int feature, boolean enable) {
            mContext.enforceCallingOrSelfPermission(
                    aoscp.platform.Manifest.permission.HARDWARE_ABSTRACTION_ACCESS, null);
            if (!isSupported(feature)) {
                Log.e(TAG, "feature " + feature + " is not supported");
                return false;
            }
            return mLunaHwImpl.set(feature, enable);
        }
    };
}