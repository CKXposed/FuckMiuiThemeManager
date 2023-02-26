package com.yuk.fuckMiuiThemeManager

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.hookMethod
import com.github.kyuubiran.ezxhelper.utils.putObject
import com.github.kyuubiran.ezxhelper.utils.unhookAll
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import io.luckypray.dexkit.DexKitBridge
import miui.drm.DrmManager
import miui.drm.ThemeReceiver
import java.lang.reflect.Method

private const val TAG = "FuckThemeManager"

class XposedInit : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        EzXHelperInit.setLogTag(TAG)
        EzXHelperInit.setToastTag(TAG)
        EzXHelperInit.initHandleLoadPackage(lpparam)
        when (lpparam.packageName) {
            "android" -> {
                var hook: List<XC_MethodHook.Unhook>? = null
                findMethod(ThemeReceiver::class.java) {
                    name == "validateTheme"
                }.hookMethod {
                    before {
                        hook = findAllMethods(DrmManager::class.java) {
                            name == "isLegal"
                        }.hookBefore {
                            it.result = DrmManager.DrmResult.DRM_SUCCESS
                        }
                    }
                    after {
                        hook?.unhookAll()
                    }
                }
            }

            "com.android.thememanager" -> {
                findAllMethods("com.android.thememanager.detail.theme.model.OnlineResourceDetail") {
                    name == "toResource"
                }.hookAfter {
                    it.thisObject.putObject("bought", true)
                }
                findAllMethods("com.android.thememanager.basemodule.views.DiscountPriceView") {
                    parameterCount == 2 && parameterTypes[0] == Int::class.javaPrimitiveType && parameterTypes[1] == Int::class.javaPrimitiveType
                }.hookBefore {
                    it.args[1] = 0
                }
                findMethod("com.miui.maml.widget.edit.MamlutilKt") {
                    name == "themeManagerSupportPaidWidget"
                }.hookAfter {
                    it.result = false
                }
                System.loadLibrary("dexkit")
                DexKitBridge.create(lpparam.appInfo.sourceDir)?.use { bridge ->
                    val map = mapOf(
                        "DrmResult" to setOf("theme", "ThemeManagerTag", "/system"),
                    )
                    val resultMap = bridge.batchFindMethodsUsingStrings {
                        queryMap(map)
                    }
                    val drmResult = resultMap["DrmResult"]!!
                    assert(drmResult.size == 1)
                    val drmResultDescriptor = drmResult.first()
                    val drmResultMethod: Method = drmResultDescriptor.getMethodInstance(lpparam.classLoader)
                    drmResultMethod.hookAfter {
                        it.result = DrmManager.DrmResult.DRM_SUCCESS
                    }
                }
            }

            "com.miui.personalassistant" -> {

                findMethod("com.miui.maml.widget.edit.MamlutilKt") {
                    name == "themeManagerSupportPaidWidget"
                }.hookAfter {
                    it.result = false
                }
                findMethod("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel") {
                    name == "isCanDirectAddMaMl"
                }.hookAfter {
                    it.result = true
                }
                findMethod("com.miui.personalassistant.picker.business.detail.utils.PickerDetailDownloadManager\$Companion") {
                    name == "isCanDownload"
                }.hookBefore {
                    it.result = true
                }
                findMethod("com.miui.personalassistant.picker.business.detail.utils.PickerDetailUtil") {
                    name == "isCanAutoDownloadMaMl"
                }.hookBefore {
                    it.result = true
                }
                findMethod("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponse") {
                    name == "isPay"
                }.hookBefore {
                    it.result = false
                }
                findMethod("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponse") {
                    name == "isBought"
                }.hookBefore {
                    it.result = true
                }
                findMethod("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponseWrapper") {
                    name == "isPay"
                }.hookBefore {
                    it.result = false
                }
                findMethod("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponseWrapper") {
                    name == "isBought"
                }.hookBefore {
                    it.result = true
                }
                findMethod("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel") {
                    name == "shouldCheckMamlBoughtState"
                }.hookAfter {
                    it.result = false
                }
                findMethod("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel") {
                    name == "isTargetPositionMamlPayAndDownloading"
                }.hookAfter {
                    it.result = false
                }
                findMethod("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel") {
                    name == "checkIsIndependentProcessWidgetForPosition"
                }.hookAfter {
                    it.result = true
                }
            }

            "com.miui.home" -> {
                findMethod("com.miui.maml.widget.edit.MamlutilKt") {
                    name == "themeManagerSupportPaidWidget"
                }.hookAfter {
                    it.result = false
                }
                findMethod("com.miui.home.launcher.gadget.MaMlPendingHostView") {
                    name == "isCanAutoStartDownload"
                }.hookAfter {
                    it.result = true
                }
            }

            else -> {
                return
            }
        }
    }

}