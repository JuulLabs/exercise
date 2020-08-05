package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

internal val activityResultContractTypeName = ClassName("androidx.activity.result.contract", "ActivityResultContract")
internal val activityTypeName = ClassName("android.app", "Activity")
internal val bundleTypeName = ClassName("android.os", "Bundle")
internal val contextTypeName = ClassName("android.content", "Context")
internal val intentTypeName = ClassName("android.content", "Intent")
internal val listTypeName = ClassName("kotlin.collections", "List")
internal val parcelTypeName = ClassName("android.os", "Parcel")
internal val stringTypeName = ClassName("kotlin", "String")
internal val suppressTypeName = ClassName("kotlin", "Suppress")

internal val bundleOfMemberName = MemberName("androidx.core.os", "bundleOf")
internal val writeToParcelMemberName = MemberName("com.juul.exercise.runtime", "writeToParcel")
