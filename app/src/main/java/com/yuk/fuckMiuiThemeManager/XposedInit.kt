package com.yuk.fuckMiuiThemeManager

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.*
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import miui.drm.DrmManager

private const val TAG = "FuckThemeManager"

class XposedInit : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        EzXHelperInit.setLogTag(TAG)
        EzXHelperInit.setToastTag(TAG)
        EzXHelperInit.initHandleLoadPackage(lpparam)
        when (lpparam.packageName) {
            "android" -> {
                EzXHelperInit.setEzClassLoader(lpparam.classLoader)
                try {
                    XposedBridge.hookAllMethods(DrmManager::class.java, "isLegal", object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            param.result = DrmManager.DrmResult.DRM_SUCCESS
                        }
                    })
                } catch (t: Throwable) {
                    XposedBridge.log(t)
                }
                try {
                    XposedBridge.hookAllMethods(DrmManager::class.java, "isRightsFileLegal", object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            param.result = true
                        }
                    })
                } catch (t: Throwable) {
                    XposedBridge.log(t)
                }
                try {
                    XposedBridge.hookAllMethods(DrmManager::class.java, "isPermanentRights", object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            param.result = true
                        }
                    })
                } catch (t: Throwable) {
                    XposedBridge.log(t)
                }

                try {
                    XposedBridge.hookAllMethods(DrmManager::class.java, "isSupportAd", object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            param.result = false
                        }
                    })
                } catch (t: Throwable) {
                    XposedBridge.log(t)
                }
                try {
                    XposedBridge.hookAllMethods(DrmManager::class.java, "setSupportAd", object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            param.args[1] = false
                        }
                    })
                } catch (t: Throwable) {
                    XposedBridge.log(t)
                }
            }

            "com.android.thememanager" -> {
                EzXHelperInit.setEzClassLoader(lpparam.classLoader)
                try {
                    findMethod("com.android.thememanager.controller.online.c") {
                        parameterCount == 1 && returnType == DrmManager.DrmResult::class.java
                    }.hookAfter {
                        it.result = DrmManager.DrmResult.DRM_SUCCESS
                    }
                } catch (t: Throwable) {
                    XposedBridge.log(t)
                }
                try {
                    findAllMethods("com.android.thememanager.detail.theme.model.OnlineResourceDetail") {
                        name == "toResource"
                    }.hookAfter {
                        it.thisObject.putObject("bought", true)
                    }
                } catch (t: Throwable) {
                    XposedBridge.log(t)
                }
                try {
                    findMethod("com.android.thememanager.basemodule.ad.model.AdInfoResponse") {
                        name == "isAdValid" && parameterCount == 1
                    }.hookAfter {
                        it.result = false
                    }
                } catch (t: Throwable) {
                    XposedBridge.log(t)
                }
                try {
                    findMethod("com.android.thememanager.basemodule.views.DiscountPriceView") {
                        name == "f" && parameterCount == 1
                    }.hookAfter {
                        it.thisObject.getObjectAs<TextView>("b").text = "免费"
                    }
                } catch (t: Throwable) {
                    XposedBridge.log(t)
                }
                hook(loadClass("com.android.thememanager.recommend.view.listview.viewholder.PureAdBannerViewHolder"))
                hook(loadClass("com.android.thememanager.recommend.view.listview.viewholder.SelfFontItemAdViewHolder"))
                hook(loadClass("com.android.thememanager.recommend.view.listview.viewholder.SelfRingtoneItemAdViewHolder"))
            }

            "com.miui.personalassistant" -> {
                EzXHelperInit.setEzClassLoader(lpparam.classLoader)
                //2023.01.18 @Weverses github
                try {
                    findMethod("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel") {
                        name == "isCanDirectAddMaMl"
                    }.hookBefore {
                        it.result = true
                    }
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isCanDirectAddMaMl success!")
                } catch (e: Throwable) {
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isCanDirectAddMaMl failed!")
                    XposedBridge.log(e)
                }
                try {
                    findMethod("com.miui.personalassistant.picker.business.detail.utils.PickerDetailDownloadManager\$Companion") {
                        name == "isCanDownload"
                    }.hookBefore {
                        it.result = true
                    }
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isCanDownload success!")
                } catch (e: Throwable) {
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isCanDownload failed!")
                    XposedBridge.log(e)
                }
                try {
                    findMethod("com.miui.personalassistant.picker.business.detail.utils.PickerDetailUtil") {
                        name == "isCanAutoDownloadMaMl"
                    }.hookBefore {
                        it.result = true
                    }
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isCanAutoDownloadMaMl success!")
                } catch (e: Throwable) {
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isCanAutoDownloadMaMl failed!")
                    XposedBridge.log(e)
                }
                try {
                    findMethod("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponse") {
                        name == "isPay"
                    }.hookBefore {
                        it.result = false
                    }
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isPay success!")
                } catch (e: Throwable) {
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isPay failed!")
                    XposedBridge.log(e)
                }
                try {
                    findMethod("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponse") {
                        name == "isBought"
                    }.hookBefore {
                        it.result = true
                    }
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isBought success!")
                } catch (e: Throwable) {
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isBought failed!")
                    XposedBridge.log(e)
                }
                try {
                    findMethod("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponseWrapper") {
                        name == "isPay"
                    }.hookBefore {
                        it.result = false
                    }
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isPay success!")
                } catch (e: Throwable) {
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isPay failed!")
                    XposedBridge.log(e)
                }
                try {
                    findMethod("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponseWrapper") {
                        name == "isBought"
                    }.hookBefore {
                        it.result = true
                    }
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isBought success!")
                } catch (e: Throwable) {
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isBought failed!")
                    XposedBridge.log(e)
                }
                try {
                    findMethod("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel") {
                        name == "shouldCheckMamlBoughtState"
                    }.hookBefore {
                        it.result = false
                    }
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-shouldCheckMamlBoughtState success!")
                } catch (e: Throwable) {
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-shouldCheckMamlBoughtState failed!")
                    XposedBridge.log(e)
                }
                try {
                    findMethod("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel") {
                        name == "isTargetPositionMamlPayAndDownloading"
                    }.hookBefore {
                        it.result = false
                    }
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isTargetPositionMamlPayAndDownloading success!")
                } catch (e: Throwable) {
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-isTargetPositionMamlPayAndDownloading failed!")
                    XposedBridge.log(e)
                }
                try {
                    findMethod("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel") {
                        name == "checkIsIndependentProcessWidgetForPosition"
                    }.hookBefore {
                        it.result = true
                    }
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-checkIsIndependentProcessWidgetForPosition success!")
                } catch (e: Throwable) {
                    XposedBridge.log("FuckMiuiThemeManager: Hook personalassistant-checkIsIndependentProcessWidgetForPosition failed!")
                    XposedBridge.log(e)
                }

            }

            else -> {
                return
            }
        }
    }

    private fun hook(clazz: Class<*>) {
        try {
            findConstructor(clazz) {
                parameterTypes.size == 2
            }.hookAfter {
                if (it.args[0] != null) {
                    val view = it.args[0] as View
                    val params = FrameLayout.LayoutParams(0, 0)
                    view.layoutParams = params
                    view.visibility = View.GONE
                }
            }
        } catch (t: Throwable) {
            XposedBridge.log(t)
        }
    }

}