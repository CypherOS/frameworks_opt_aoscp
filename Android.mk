# Copyright (C) 2017 CypherOS
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := org.aoscp.framework

LOCAL_SRC_FILES := $(call all-java-files-under,src)

LOCAL_SRC_FILES += $(call all-Iaidl-files-under,src)

LOCAL_JAVA_LIBRARIES := \
    android-support-v4 \
    android-support-annotations

aoscp_platform_res := APPS/org.aoscp.framework-res_intermediates/src

LOCAL_INTERMEDIATE_SOURCES := \
    $(aoscp_platform_res)/aoscp/R.java \
    $(aoscp_platform_res)/org/aoscp/framework/internal/R.java \
	$(aoscp_platform_res)/aoscp/Manifest.java

include $(BUILD_JAVA_LIBRARY)
aoscp_framework_module := $(LOCAL_INSTALLED_MODULE)
# Make sure that R.java and Manifest.java are built before we build
# the source for this library.
aoscp_framework_res_R_stamp := \
    $(call intermediates-dir-for,APPS,org.aoscp.framework-res,,COMMON)/src/R.stamp
$(full_classes_compiled_jar): $(aoscp_framework_res_R_stamp)
$(built_dex_intermediate): $(aoscp_framework_res_R_stamp)

$(aoscp_framework_module): | $(dir $(aoscp_framework_module))org.aoscp.framework-res.apk

aoscp_framework_built := $(call java-lib-deps, org.aoscp.framework)

include $(call all-makefiles-under,$(LOCAL_PATH))