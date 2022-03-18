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

static JNINativeMethod sNativeMethods[] = {
        {"cS", "(Landroid/content/Context;)Z", (void *) cS},
};

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, __attribute__((unused)) void *reserved) {

#ifdef DEBUG
    LOGE("------------ 🚨 WARNING 🚨 -------------");
    LOGE("libCvNative compiled in DEBUG mode");
    LOGE("---------------------------------------");
#endif

    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_EVERSION;
    }

    Name cvNativeClassJava(CV_NATIVE_CLASS_JAVA, CV_NATIVE_CLASS_JAVA_LEN);
    jclass cvNativeClass = env->FindClass(cvNativeClassJava.getName());

    if (env->RegisterNatives(cvNativeClass, sNativeMethods,
                             sizeof(sNativeMethods) / sizeof(sNativeMethods[0])) < 0) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}

jboolean JNICALL cS(JNIEnv *env, __attribute__((unused)) jobject thiz, jobject context) {

    Name methodName(CONTEXT_CLASS_JAVA, CONTEXT_CLASS_JAVA_LEN);
    jclass activityClass = env->FindClass(methodName.getName());

    methodName.setName(GET_PACKAGE_MANAGER_METHOD, GET_PACKAGE_MANAGER_METHOD_LEN);

    Name params(GET_PACKAGE_MANAGER_PARAMS, GET_PACKAGE_MANAGER_PARAMS_LEN);
    jmethodID getPackageManagerMid = env->GetMethodID(activityClass, methodName.getName(),
                                                      params.getName());

    methodName.setName(GET_PACKAGE_NAME_METHOD, GET_PACKAGE_NAME_METHOD_LEN);
    params.setName(GET_PACKAGE_NAME_PARAMS, GET_PACKAGE_NAME_PARAMS_LEN);
    jmethodID getPackageNameMid = env->GetMethodID(activityClass, methodName.getName(),
                                                   params.getName());
    jobject packageManagerObject = env->CallObjectMethod(context, getPackageManagerMid);

    // packageManager = context.getPackageManager()

    methodName.setName(PACKAGE_MANAGER_CLASS_JAVA, PACKAGE_MANAGER_CLASS_JAVA_LEN);
    jclass packageManagerClass = env->FindClass(methodName.getName());

    methodName.setName(GET_PACKAGE_INFO_METHOD, GET_PACKAGE_INFO_METHOD_LEN);
    params.setName(GET_PACKAGE_INFO_PARAMS, GET_PACKAGE_INFO_PARAMS_LEN);
    jmethodID getPackageInfoMid = env->GetMethodID(packageManagerClass, methodName.getName(),
                                                   params.getName());
    auto packageNameString = (jstring) env->CallObjectMethod(context, getPackageNameMid);

    // packageName = context.getPackageName()

    methodName.setName(GET_SIGNATURES_METHOD, GET_SIGNATURES_METHOD_LEN);
    params.setName(GET_SIGNATURES_PARAMS, GET_SIGNATURES_PARAMS_LEN);
    jfieldID getSignaturesFid = env->GetStaticFieldID(packageManagerClass, methodName.getName(),
                                                      params.getName());
    jint GET_SIGNATURES = env->GetStaticIntField(packageManagerClass, getSignaturesFid);

    // getSignatures = PackageManager.GET_SIGNATURES

    jobject packageInfoObject = env->CallObjectMethod(packageManagerObject,
                                                      getPackageInfoMid,
                                                      packageNameString, GET_SIGNATURES);

    // packageInfo = packageManager.getPackageInfo(packageName, getSignatures)

    methodName.setName(PACKAGE_INFO_CLASS_JAVA, PACKAGE_INFO_CLASS_JAVA_LEN);
    jclass packageInfoClass = env->FindClass(methodName.getName());

    methodName.setName(SIGNATURES_FIELD, SIGNATURES_FIELD_LEN);
    params.setName(SIGNATURE_FIELD_RETURN_VALUE, SIGNATURE_FIELD_RETURN_VALUE_LEN);
    jfieldID signatureFid = env->GetFieldID(packageInfoClass, methodName.getName(),
                                            params.getName());

    auto signatureArrayObject = (jobjectArray) (env->GetObjectField(
            packageInfoObject,
            signatureFid));

    // signatures = packageInfo.getSignatures()
    jobject signatureObject = env->GetObjectArrayElement(signatureArrayObject, 0);

    // signature = signature[0]

    methodName.setName(SIGNATURE_CLASS_JAVA, SIGNATURE_CLASS_JAVA_LEN);
    jclass signatureClass = env->FindClass(methodName.getName());

    methodName.setName(MESSAGE_DIGEST_CLASS_JAVA, MESSAGE_DIGEST_CLASS_JAVA_LEN);
    jclass messageDigestClass = env->FindClass(methodName.getName());
    methodName.setName(GET_INSTANCE_METHOD, GET_INSTANCE_METHOD_LEN);
    params.setName(GET_INSTANCE_PARAMS, GET_INSTANCE_PARAMS_LEN);
    jmethodID messageDigestGetInstanceMid = env->GetStaticMethodID(messageDigestClass,
                                                                   methodName.getName(),
                                                                   params.getName());

    params.setName(SHA_256_PARAM, SHA_256_PARAM_LEN);
    jobject messageDigestObject = env->CallStaticObjectMethod(messageDigestClass,
                                                              messageDigestGetInstanceMid,
                                                              env->NewStringUTF(params.getName()));

    // messageDigest = MessageDigest.getInstance("SHA-1")

    methodName.setName(DIGEST_METHOD, DIGEST_METHOD_LEN);
    params.setName(DIGEST_PARAMS, DIGEST_PARAMS_LEN);

    jmethodID digestMid = env->GetMethodID(messageDigestClass, methodName.getName(),
                                           params.getName());

    methodName.setName(TO_BYTE_ARRAY_METHOD, TO_BYTE_ARRAY_METHOD_LEN);
    params.setName(TO_BYTE_ARRAY_PARAMS, TO_BYTE_ARRAY_PARAMS_LEN);
    jmethodID toByteArrayMid = env->GetMethodID(signatureClass, methodName.getName(),
                                                params.getName());

    auto signatureByteArray = (jbyteArray) (env->CallObjectMethod(signatureObject,
                                                                  toByteArrayMid));
    // signatureByteArray = signature.toByteArray()

    auto digest = (jbyteArray) (env->CallObjectMethod(messageDigestObject,
                                                      digestMid,
                                                      signatureByteArray));

    // digest = messageDigest.digest(signatureByteArray)

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
    jbyte expectedSignature[] = {(jbyte) 0xA9, (jbyte) 0x0B, (jbyte) 0x9B, (jbyte) 0xEA,
                                 (jbyte) 0xBD,
                                 (jbyte) 0xC8, (jbyte) 0xBA, (jbyte) 0x19, (jbyte) 0xD0,
                                 (jbyte) 0x1C,
                                 (jbyte) 0xAD, (jbyte) 0x49, (jbyte) 0x16, (jbyte) 0x33,
                                 (jbyte) 0x54,
                                 (jbyte) 0xFC, (jbyte) 0x84, (jbyte) 0x68, (jbyte) 0xBF,
                                 (jbyte) 0x47
    };
#endif

    int result = memcmp(digestArray, expectedSignature, SIGNATURE_HASH_LENGTH);

    if (result == 0) {
        return JNI_TRUE;
    }

    return JNI_FALSE;
}
