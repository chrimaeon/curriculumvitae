/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <cstring>
#include <jni.h>

#include "encodedNames.h"
#include "Name.h"

#ifdef DEBUG

#include "logger.h"

#endif

#define SIGNATURE_HASH_LENGTH 32

jboolean JNICALL cS(JNIEnv *env, __attribute__((unused)) jobject thiz, jobject context);

jobject getSignatureOld(JNIEnv *, jclass, jobject, jstring);

jobject getSignatureTiramisu(JNIEnv *, jclass, jobject, jstring);

static JNINativeMethod sNativeMethods[] = {
        {"cS", "(Landroid/content/Context;)Z", (void *) cS},
};

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, __attribute__((unused)) void *reserved) {

#ifdef DEBUG
    LOGW("┌─────────── 🚨 WARNING 🚨 ───────────┐");
    LOGW("│ libCvNative compiled in DEBUG mode │");
    LOGW("└────────────────────────────────────┘");
#endif

    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_EVERSION;
    }

    Name cvNativeClassJava(CV_NATIVE_CLASS_JAVA, CV_NATIVE_CLASS_JAVA_LEN);
    jclass cvNativeClass = env->FindClass(cvNativeClassJava.getName());

    if (env->RegisterNatives(cvNativeClass,
                             sNativeMethods,
                             sizeof(sNativeMethods) / sizeof(sNativeMethods[0])) < 0) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}

jboolean JNICALL cS(JNIEnv *env, __attribute__((unused)) jobject thiz, jobject context) {

    Name methodName(CONTEXT_CLASS_JAVA, CONTEXT_CLASS_JAVA_LEN);
    auto activityClass = env->FindClass(methodName.getName());

    methodName.setName(GET_PACKAGE_MANAGER_METHOD, GET_PACKAGE_MANAGER_METHOD_LEN);

    Name params(GET_PACKAGE_MANAGER_PARAMS, GET_PACKAGE_MANAGER_PARAMS_LEN);
    auto getPackageManagerMid = env->GetMethodID(activityClass, methodName.getName(),
                                                 params.getName());

    methodName.setName(GET_PACKAGE_NAME_METHOD, GET_PACKAGE_NAME_METHOD_LEN);
    params.setName(GET_PACKAGE_NAME_PARAMS, GET_PACKAGE_NAME_PARAMS_LEN);
    auto getPackageNameMid = env->GetMethodID(activityClass, methodName.getName(),
                                              params.getName());
    auto packageManagerObject = env->CallObjectMethod(context, getPackageManagerMid);

    methodName.setName(PACKAGE_MANAGER_CLASS_JAVA, PACKAGE_MANAGER_CLASS_JAVA_LEN);
    auto packageManagerClass = env->FindClass(methodName.getName());

    auto packageNameString = (jstring) env->CallObjectMethod(context, getPackageNameMid);

    jobject signatureObject;

    if (android_get_device_api_level() >= __ANDROID_API_T__) {
        signatureObject = getSignatureTiramisu(env, packageManagerClass, packageManagerObject,
                                               packageNameString);
    } else {
        signatureObject = getSignatureOld(env, packageManagerClass, packageManagerObject,
                                          packageNameString);
    }

    methodName.setName(SIGNATURE_CLASS_JAVA, SIGNATURE_CLASS_JAVA_LEN);
    auto signatureClass = env->FindClass(methodName.getName());

    methodName.setName(MESSAGE_DIGEST_CLASS_JAVA, MESSAGE_DIGEST_CLASS_JAVA_LEN);
    auto messageDigestClass = env->FindClass(methodName.getName());
    methodName.setName(GET_INSTANCE_METHOD, GET_INSTANCE_METHOD_LEN);
    params.setName(GET_INSTANCE_PARAMS, GET_INSTANCE_PARAMS_LEN);
    auto messageDigestGetInstanceMid = env->GetStaticMethodID(messageDigestClass,
                                                              methodName.getName(),
                                                              params.getName());

    params.setName(SHA_256_PARAM, SHA_256_PARAM_LEN);
    auto messageDigestObject = env->CallStaticObjectMethod(messageDigestClass,
                                                           messageDigestGetInstanceMid,
                                                           env->NewStringUTF(params.getName()));
    methodName.setName(DIGEST_METHOD, DIGEST_METHOD_LEN);
    params.setName(DIGEST_PARAMS, DIGEST_PARAMS_LEN);

    auto digestMid = env->GetMethodID(messageDigestClass, methodName.getName(),
                                      params.getName());

    methodName.setName(TO_BYTE_ARRAY_METHOD, TO_BYTE_ARRAY_METHOD_LEN);
    params.setName(TO_BYTE_ARRAY_PARAMS, TO_BYTE_ARRAY_PARAMS_LEN);
    auto toByteArrayMid = env->GetMethodID(signatureClass, methodName.getName(),
                                           params.getName());

    auto signatureByteArray = (jbyteArray) (env->CallObjectMethod(signatureObject,
                                                                  toByteArrayMid));
    auto digest = (jbyteArray) (env->CallObjectMethod(messageDigestObject,
                                                      digestMid,
                                                      signatureByteArray));

    jbyte *digestArray = env->GetByteArrayElements(digest, JNI_FALSE);


#ifdef DEBUG
    jbyte expectedSignature[] = {(jbyte) 0x34, (jbyte) 0x3A, (jbyte) 0xCA, (jbyte) 0x7D,
                                 (jbyte) 0x16, (jbyte) 0xEA, (jbyte) 0x8E, (jbyte) 0xB4,
                                 (jbyte) 0x97, (jbyte) 0x26, (jbyte) 0xD0, (jbyte) 0xD3,
                                 (jbyte) 0xE1, (jbyte) 0x5B, (jbyte) 0xDE, (jbyte) 0x20,
                                 (jbyte) 0xA7, (jbyte) 0xBF, (jbyte) 0xEC, (jbyte) 0xEE,
                                 (jbyte) 0x22, (jbyte) 0x51, (jbyte) 0x7D, (jbyte) 0x6A,
                                 (jbyte) 0x20, (jbyte) 0x80, (jbyte) 0x37, (jbyte) 0x9E,
                                 (jbyte) 0xBD, (jbyte) 0xF8, (jbyte) 0x22, (jbyte) 0xAF
    };
#else
    jbyte expectedSignature[] = {(jbyte) 0xA1, (jbyte) 0x0E, (jbyte) 0xE5, (jbyte) 0x66,
                                 (jbyte) 0xEC, (jbyte) 0xD9, (jbyte) 0xC8, (jbyte) 0x02,
                                 (jbyte) 0x9B, (jbyte) 0x86, (jbyte) 0x53, (jbyte) 0x6D,
                                 (jbyte) 0x03, (jbyte) 0x1B, (jbyte) 0xC0, (jbyte) 0x93,
                                 (jbyte) 0xCE, (jbyte) 0x17, (jbyte) 0x22, (jbyte) 0x45,
                                 (jbyte) 0xA5, (jbyte) 0x09, (jbyte) 0xC6, (jbyte) 0x69,
                                 (jbyte) 0xF3, (jbyte) 0xBB, (jbyte) 0x24, (jbyte) 0x18,
                                 (jbyte) 0xE2, (jbyte) 0x84, (jbyte) 0xD7, (jbyte) 0x9D,
    };
#endif

    int result = memcmp(digestArray, expectedSignature, SIGNATURE_HASH_LENGTH);

    if (digestArray != nullptr) {
        env->ReleaseByteArrayElements(digest, digestArray, JNI_ABORT);
    }

    if (result == 0) {
        return JNI_TRUE;
    }

    return JNI_FALSE;
}

jobject getSignatureTiramisu(JNIEnv *env, jclass packageManagerClass, jobject packageManagerObject,
                             jstring packageNameString) {
    Name methodName(GET_PACKAGE_INFO_METHOD, GET_PACKAGE_INFO_METHOD_LEN);
    Name params(GET_PACKAGE_INFO_TIRAMISU_PARAMS, GET_PACKAGE_INFO_TIRAMISU_PARAMS_LEN);

    auto getPackageInfoMid = env->GetMethodID(packageManagerClass, methodName.getName(),
                                              params.getName());

    methodName.setName(PACKAGE_INFO_FLAGS_CLASS_JAVA, PACKAGE_INFO_FLAGS_CLASS_JAVA_LEN);
    auto packageInfoFlagsClass = env->FindClass(methodName.getName());

    methodName.setName(PACKAGE_INFO_FLAGS_OF_METHOD, PACKAGE_INFO_FLAGS_OF_METHOD_LEN);
    params.setName(PACKAGE_INFO_FLAGS_OF_PARAMS, PACKAGE_INFO_FLAGS_OF_PARAMS_LEN);

    auto packageInfoOfMid = env->GetStaticMethodID(packageInfoFlagsClass,
                                                   methodName.getName(),
                                                   params.getName());

    methodName.setName(GET_SIGNING_CERTIFICATES_METHOD, GET_SIGNING_CERTIFICATES_METHOD_LEN);
    params.setName(GET_SIGNING_CERTIFICATES_PARAMS, GET_SIGNING_CERTIFICATES_PARAMS_LEN);
    auto getSigningCertificatesFid = env->GetStaticFieldID(packageManagerClass,
                                                           methodName.getName(),
                                                           params.getName());

    auto GET_SIGNING_CERTIFICATES = env->GetStaticIntField(packageManagerClass,
                                                           getSigningCertificatesFid);

    auto packageInfoFlags = env->CallStaticObjectMethod(packageInfoFlagsClass,
                                                        packageInfoOfMid,
                                                        (jlong) GET_SIGNING_CERTIFICATES);

    auto packageInfoObject = env->CallObjectMethod(packageManagerObject,
                                                   getPackageInfoMid,
                                                   packageNameString, packageInfoFlags);

    methodName.setName(PACKAGE_INFO_CLASS_JAVA, PACKAGE_INFO_CLASS_JAVA_LEN);
    auto packageInfoClass = env->FindClass(methodName.getName());

    methodName.setName(SIGNING_INFO_FIELD, SIGNING_INFO_FIELD_LEN);
    params.setName(SIGNING_INFO_RETURN_PARAMS, SIGNING_INFO_RETURN_PARAMS_LEN);
    auto signingInfoFid = env->GetFieldID(packageInfoClass, methodName.getName(),
                                          params.getName());

    auto signingInfoObject = env->GetObjectField(packageInfoObject, signingInfoFid);

    methodName.setName(SIGNING_INFO_CLASS_JAVA, SIGNING_INFO_CLASS_JAVA_LEN);
    auto signingInfoClass = env->FindClass(methodName.getName());

    methodName.setName(GET_APK_CONTENTS_SIGNERS_METHOD, GET_APK_CONTENTS_SIGNERS_METHOD_LEN);
    params.setName(GET_APK_CONTENTS_SIGNERS_PARAMS, GET_APK_CONTENTS_SIGNERS_PARAMS_LEN);

    auto apkContentsSignersMid = env->GetMethodID(signingInfoClass, methodName.getName(),
                                                  params.getName());

    auto signatureArrayObject = (jobjectArray) (env->CallObjectMethod(
            signingInfoObject,
            apkContentsSignersMid));

    return env->GetObjectArrayElement(signatureArrayObject, 0);
}

jobject getSignatureOld(JNIEnv *env, jclass packageManagerClass, jobject packageManagerObject,
                        jstring packageNameString) {
    Name methodName(GET_PACKAGE_INFO_METHOD, GET_PACKAGE_INFO_METHOD_LEN);
    Name params(GET_PACKAGE_INFO_PARAMS, GET_PACKAGE_INFO_PARAMS_LEN);
    auto getPackageInfoMid = env->GetMethodID(packageManagerClass, methodName.getName(),
                                              params.getName());

    methodName.setName(GET_SIGNATURES_METHOD, GET_SIGNATURES_METHOD_LEN);
    params.setName(GET_SIGNATURES_PARAMS, GET_SIGNATURES_PARAMS_LEN);
    auto getSignaturesFid = env->GetStaticFieldID(packageManagerClass, methodName.getName(),
                                                  params.getName());
    auto GET_SIGNATURES = env->GetStaticIntField(packageManagerClass, getSignaturesFid);
    auto packageInfoObject = env->CallObjectMethod(packageManagerObject,
                                                   getPackageInfoMid,
                                                   packageNameString, GET_SIGNATURES);

    methodName.setName(PACKAGE_INFO_CLASS_JAVA, PACKAGE_INFO_CLASS_JAVA_LEN);
    auto packageInfoClass = env->FindClass(methodName.getName());

    methodName.setName(SIGNATURES_FIELD, SIGNATURES_FIELD_LEN);
    params.setName(SIGNATURE_FIELD_RETURN_VALUE, SIGNATURE_FIELD_RETURN_VALUE_LEN);
    auto signatureFid = env->GetFieldID(packageInfoClass, methodName.getName(),
                                        params.getName());

    auto signatureArrayObject = (jobjectArray) (env->GetObjectField(
            packageInfoObject,
            signatureFid));

    return env->GetObjectArrayElement(signatureArrayObject, 0);
}
