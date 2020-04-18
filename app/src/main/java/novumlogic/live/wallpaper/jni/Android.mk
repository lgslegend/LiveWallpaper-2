LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
 
LOCAL_MODULE    := gdx
LOCAL_C_INCLUDES := 
 
LOCAL_CFLAGS := $(LOCAL_C_INCLUDES:%=-I%) -O2 -Wall -D__ANDROID__
LOCAL_CPPFLAGS := $(LOCAL_C_INCLUDES:%=-I%) -O2 -Wall -D__ANDROID__
LOCAL_LDLIBS := -lm -lGLESv2 -llog
LOCAL_ARM_MODE  := arm
 
LOCAL_SRC_FILES := AndroidGL20.cpp\
	novumlogic.live.wallpaper.gdx.graphics.g2d.Gdx2DPixmap.cpp\
	novumlogic.live.wallpaper.gdx.graphics.glutils.ETC1.cpp\
	novumlogic.live.wallpaper.gdx.math.Matrix4.cpp\
	novumlogic.live.wallpaper.gdx.utils.BufferUtils.cpp\
	etc1/etc1_utils.cpp\
	gdx2d/gdx2d.c\
	gdx2d/jpgd.cpp\
	gdx2d/jpgd_c.cpp\
	memcpy_wrap.c
 
include $(BUILD_SHARED_LIBRARY)
