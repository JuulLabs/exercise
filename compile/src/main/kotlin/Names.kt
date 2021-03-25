package com.juul.exercise.compile

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

internal val activityResultContractTypeName = ClassName("androidx.activity.result.contract", "ActivityResultContract")
internal val activityTypeName = ClassName("android.app", "Activity")
internal val bundleTypeName = ClassName("android.os", "Bundle")
internal val byteArrayTypeName = ClassName("kotlin", "ByteArray")
internal val contextTypeName = ClassName("android.content", "Context")
internal val intentTypeName = ClassName("android.content", "Intent")
internal val listTypeName = ClassName("kotlin.collections", "List")
internal val parcelTypeName = ClassName("android.os", "Parcel")
internal val stringTypeName = ClassName("kotlin", "String")
internal val suppressTypeName = ClassName("kotlin", "Suppress")

internal val bundleOfMemberName = MemberName("androidx.core.os", "bundleOf")
internal val createFromMarshalledBytesMemberName = MemberName("com.juul.exercise.runtime", "createFromMarshalledBytes")
internal val createFromMarshalledBytesOrNullMemberName = MemberName("com.juul.exercise.runtime", "createFromMarshalledBytesOrNull")
internal val writeToMarshalledBytesMemberName = MemberName("com.juul.exercise.runtime", "writeToMarshalledBytes")
internal val writeToMarshalledBytesOrNullMemberName = MemberName("com.juul.exercise.runtime", "writeToMarshalledBytesOrNull")
