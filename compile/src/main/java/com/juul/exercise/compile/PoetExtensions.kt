package com.juul.exercise.compile

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

internal fun FileSpec.Builder.addClass(name: ClassName, builderActions: TypeSpec.Builder.() -> Unit) =
    addType(TypeSpec.classBuilder(name).apply(builderActions).build())

internal fun FileSpec.Builder.addClass(name: String, builderActions: TypeSpec.Builder.() -> Unit) =
    addType(TypeSpec.classBuilder(name).apply(builderActions).build())

internal fun FileSpec.Builder.addFunction(name: String, builderActions: FunSpec.Builder.() -> Unit) =
    addFunction(FunSpec.builder(name).apply(builderActions).build())

internal fun FileSpec.Builder.addProperty(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    builderActions: PropertySpec.Builder.() -> Unit
) = addProperty(PropertySpec.builder(name, type, *modifiers).apply(builderActions).build())

internal fun FileSpec.Companion.build(
    packageName: String,
    fileName: String,
    builderActions: FileSpec.Builder.() -> Unit
) = builder(packageName, fileName).apply(builderActions).build()

internal fun FunSpec.Builder.addAnnotation(
    type: ClassName,
    builderActions: AnnotationSpec.Builder.() -> Unit
) = addAnnotation(AnnotationSpec.builder(type).apply(builderActions).build())

internal fun FunSpec.Builder.addParameter(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    builderActions: ParameterSpec.Builder.() -> Unit
) = addParameter(ParameterSpec.builder(name, type, *modifiers).apply(builderActions).build())

internal fun FunSpec.Companion.build(name: String, builderActions: FunSpec.Builder.() -> Unit) =
    builder(name).apply(builderActions).build()

internal fun ParameterSpec.Companion.build(
    name: String,
    type: TypeName,
    builderActions: ParameterSpec.Builder.() -> Unit
) = builder(name, type).apply(builderActions).build()

internal fun PropertySpec.Builder.getter(builderActions: FunSpec.Builder.() -> Unit) =
    getter(FunSpec.getterBuilder().apply(builderActions).build())

internal fun PropertySpec.Companion.build(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    builderActions: PropertySpec.Builder.() -> Unit
) = builder(name, type, *modifiers).apply(builderActions).build()

internal fun TypeSpec.Builder.addClass(name: String, builderActions: TypeSpec.Builder.() -> Unit) =
    addType(TypeSpec.classBuilder(name).apply(builderActions).build())

internal fun TypeSpec.Builder.addConstructor(builderActions: FunSpec.Builder.() -> Unit) =
    addFunction(FunSpec.constructorBuilder().apply(builderActions).build())

internal fun TypeSpec.Builder.addInitializerBlock(builderActions: CodeBlock.Builder.() -> Unit) =
    addInitializerBlock(CodeBlock.builder().apply(builderActions).build())

internal fun TypeSpec.Builder.addFunction(name: String, builderActions: FunSpec.Builder.() -> Unit) =
    addFunction(FunSpec.builder(name).apply(builderActions).build())

internal fun TypeSpec.Builder.addProperty(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    builderActions: PropertySpec.Builder.() -> Unit
) = addProperty(PropertySpec.builder(name, type, *modifiers).apply(builderActions).build())

internal fun TypeSpec.Builder.primaryConstructor(builderActions: FunSpec.Builder.() -> Unit) =
    primaryConstructor(FunSpec.constructorBuilder().apply(builderActions).build())

internal fun TypeSpec.Companion.buildClass(name: String, builderActions: TypeSpec.Builder.() -> Unit) =
    classBuilder(name).apply(builderActions).build()
