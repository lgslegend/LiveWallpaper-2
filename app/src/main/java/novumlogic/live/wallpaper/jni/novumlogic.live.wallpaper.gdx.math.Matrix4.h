/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class novumlogic_live_wallpaper_gdx_math_Matrix4 */

#ifndef _Included_novumlogic_live_wallpaper_gdx_math_Matrix4
#define _Included_novumlogic_live_wallpaper_gdx_math_Matrix4
#ifdef __cplusplus
extern "C" {
#endif
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_serialVersionUID
#define novumlogic_live_wallpaper_gdx_math_Matrix4_serialVersionUID -2717655254359579617LL
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M00
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M00 0L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M01
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M01 4L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M02
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M02 8L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M03
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M03 12L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M10
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M10 1L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M11
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M11 5L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M12
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M12 9L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M13
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M13 13L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M20
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M20 2L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M21
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M21 6L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M22
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M22 10L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M23
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M23 14L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M30
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M30 3L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M31
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M31 7L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M32
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M32 11L
#undef novumlogic_live_wallpaper_gdx_math_Matrix4_M33
#define novumlogic_live_wallpaper_gdx_math_Matrix4_M33 15L
/*
 * Class:     novumlogic_live_wallpaper_gdx_math_Matrix4
 * Method:    mul
 * Signature: ([F[F)V
 */
JNIEXPORT void JNICALL Java_novumlogic_live_wallpaper_gdx_math_Matrix4_mul
  (JNIEnv *, jclass, jfloatArray, jfloatArray);

/*
 * Class:     novumlogic_live_wallpaper_gdx_math_Matrix4
 * Method:    mulVec
 * Signature: ([F[F)V
 */
JNIEXPORT void JNICALL Java_novumlogic_live_wallpaper_gdx_math_Matrix4_mulVec___3F_3F
  (JNIEnv *, jclass, jfloatArray, jfloatArray);

/*
 * Class:     novumlogic_live_wallpaper_gdx_math_Matrix4
 * Method:    mulVec
 * Signature: ([F[FIII)V
 */
JNIEXPORT void JNICALL Java_novumlogic_live_wallpaper_gdx_math_Matrix4_mulVec___3F_3FIII
  (JNIEnv *, jclass, jfloatArray, jfloatArray, jint, jint, jint);

/*
 * Class:     novumlogic_live_wallpaper_gdx_math_Matrix4
 * Method:    prj
 * Signature: ([F[F)V
 */
JNIEXPORT void JNICALL Java_novumlogic_live_wallpaper_gdx_math_Matrix4_prj___3F_3F
  (JNIEnv *, jclass, jfloatArray, jfloatArray);

/*
 * Class:     novumlogic_live_wallpaper_gdx_math_Matrix4
 * Method:    prj
 * Signature: ([F[FIII)V
 */
JNIEXPORT void JNICALL Java_novumlogic_live_wallpaper_gdx_math_Matrix4_prj___3F_3FIII
  (JNIEnv *, jclass, jfloatArray, jfloatArray, jint, jint, jint);

/*
 * Class:     novumlogic_live_wallpaper_gdx_math_Matrix4
 * Method:    rot
 * Signature: ([F[F)V
 */
JNIEXPORT void JNICALL Java_novumlogic_live_wallpaper_gdx_math_Matrix4_rot___3F_3F
  (JNIEnv *, jclass, jfloatArray, jfloatArray);

/*
 * Class:     novumlogic_live_wallpaper_gdx_math_Matrix4
 * Method:    rot
 * Signature: ([F[FIII)V
 */
JNIEXPORT void JNICALL Java_novumlogic_live_wallpaper_gdx_math_Matrix4_rot___3F_3FIII
  (JNIEnv *, jclass, jfloatArray, jfloatArray, jint, jint, jint);

/*
 * Class:     novumlogic_live_wallpaper_gdx_math_Matrix4
 * Method:    inv
 * Signature: ([F)Z
 */
JNIEXPORT jboolean JNICALL Java_novumlogic_live_wallpaper_gdx_math_Matrix4_inv
  (JNIEnv *, jclass, jfloatArray);

/*
 * Class:     novumlogic_live_wallpaper_gdx_math_Matrix4
 * Method:    det
 * Signature: ([F)F
 */
JNIEXPORT jfloat JNICALL Java_novumlogic_live_wallpaper_gdx_math_Matrix4_det
  (JNIEnv *, jclass, jfloatArray);

#ifdef __cplusplus
}
#endif
#endif
