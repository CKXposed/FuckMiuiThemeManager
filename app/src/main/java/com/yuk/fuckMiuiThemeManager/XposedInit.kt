package com.yuk.fuckMiuiThemeManager

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findConstructor
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.getObjectAs
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.loadClass
import com.github.kyuubiran.ezxhelper.utils.putObject
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import miui.drm.DrmManager

private const val TAG = "FuckThemeManager"

class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {

    override fun initZygote(startupParam: StartupParam) {
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

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        EzXHelperInit.setLogTag(TAG)
        EzXHelperInit.setToastTag(TAG)
        EzXHelperInit.initHandleLoadPackage(lpparam)
        when (lpparam.packageName) {
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
                try {
                    "com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponse".findClass().hookAfterAllConstructors {
                        it.thisObject.setBooleanField("isPay", true)
                        it.thisObject.setBooleanField("isBought", true)
                        it.thisObject.setBooleanField("isAuthorityPass", true)
                        it.thisObject.setLongField("priceInCent", 0L)
                        it.thisObject.setObjectField("authorityResult", DrmManager.DrmResult.DRM_SUCCESS)
                    }
                } catch (t: Throwable) {
                    XposedBridge.log(t)
                }
                try {
                    ("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponseWrapper").findClass().hookAfterAllConstructors {
                        it.thisObject.setBooleanField("isPay", true)
                        it.thisObject.setBooleanField("isBought", true)
                        it.thisObject.setBooleanField("isStartMaMlDownload", true)
                        it.thisObject.setLongField("priceInCent", 0L)
                    }
                } catch (t: Throwable) {
                    XposedBridge.log(t)
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