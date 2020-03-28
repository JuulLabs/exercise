package com.juullabs.exercise.compile

import javax.lang.model.element.AnnotationMirror

internal operator fun AnnotationMirror.get(key: String): Any? =
    elementValues.entries.singleOrNull { (k, _) -> k.simpleName.toString() == key }?.value?.value
