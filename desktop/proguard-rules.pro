# Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#      http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-optimizationpasses 5
-allowaccessmodification
-dontobfuscate

-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn org.slf4j.impl.**
-dontwarn android.annotation.SuppressLint
-dontwarn kotlinx.atomicfu.AtomicRef

-ignorewarnings
-verbose

-keep public class MainKt {
    public static void main(java.lang.String[]);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

#-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
#  public static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
#  public static void checkFieldIsNotNull(java.lang.Object, java.lang.String);
#  public static void checkFieldIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
#  public static void checkNotNull(java.lang.Object);
#  public static void checkNotNull(java.lang.Object, java.lang.String);
#  public static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
#  public static void checkNotNullParameter(java.lang.Object, java.lang.String);
#  public static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
#  public static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String);
#  public static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
#  public static void throwUninitializedPropertyAccessException(java.lang.String);
#}


