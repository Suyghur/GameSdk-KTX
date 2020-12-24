//
// Created by #Suyghur, on 2020/12/18.
//

#include "include/constant.h"
#include "rsa_utils.h"
#include "include/logger.h"
#include "include/jtools.h"

string RSA::encrypt_by_public_key(JNIEnv *env, const string &raw) {
    jclass _clz = env->FindClass(RSA_CLZ_NAME);
    if (_clz == NULL) {
        Logger::loge("rsa impl clz is NULL !!!");
        return "";
    }

    const char *method_name = "encryptByPublicKey";
    const char *sig = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jkey = env->NewStringUTF(RSA_PUBLIC_1024_X509_PEM);
    jstring jraw = env->NewStringUTF(raw.c_str());
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, jkey, jraw);
    return JTools::jstring2str(env, jresult);
}


string RSA::decrypt_by_public_key(JNIEnv *env, const string & enc) {
    jclass _clz = env->FindClass(RSA_CLZ_NAME);
    if (_clz == NULL) {
        Logger::loge("rsa impl clz is NULL !!!");
        return "";
    }

    const char *method_name = "decryptByPublicKey";
    const char *sig = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jkey = env->NewStringUTF(RSA_PUBLIC_1024_X509_PEM);
    jstring jenc = env->NewStringUTF(enc.c_str());
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, jkey, jenc);
    return JTools::jstring2str(env, jresult);
}