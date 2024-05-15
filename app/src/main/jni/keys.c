#include <jni.h>


JNIEXPORT jstring
JNICALL
Java_com_inaxdevelopers_weatherforecast_fragments_HomeFragment_getApiKey(JNIEnv
                                                                         *env,
                                                                         jobject thiz
) {

}

JNIEXPORT jstring JNICALL
Java_com_inaxdevelopers_weatherforecast_MainActivity_getApiKey(JNIEnv *env, jobject thiz) {
    return (*env)->
            NewStringUTF(env,
                         "a8a37db71ea612cdd8c0e13c23416a7a");
}