/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#ifndef __LOGGER_H_
#define __LOGGER_H_

#ifdef DEBUG

#include "android/log.h"

#define LOG_TAG "libCvNative"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))

#else

#define LOGD(...) ((void)0)
#define LOGE(...) ((void)0)
#define LOGI(...) ((void)0)

#endif //DEBUG
#endif //__LOGGER_H_
