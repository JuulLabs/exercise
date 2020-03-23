package com.juullabs.exercise.compile

import javax.lang.model.element.AnnotationMirror

private const val ACTIVITY_RESULT_CANCELED = 0
private const val ACTIVITY_RESULT_FIRST_USER = 1
private const val ACTIVITY_RESULT_OK = -1
private const val NAME_CANCELED = "Canceled"
private const val NAME_OK = "Ok"

private val DEFAULT_CANCELED_KIND = ResultKind(ACTIVITY_RESULT_CANCELED, NAME_CANCELED, emptyList())
private val DEFAULT_OK_KIND = ResultKind(ACTIVITY_RESULT_OK, NAME_OK, emptyList())

internal data class ResultKind(
    val code: Int,
    val name: String,
    val params: List<Parameter>
)

internal fun getResultKindsForMirror(mirror: AnnotationMirror): List<ResultKind> {
    var nextResultId = ACTIVITY_RESULT_FIRST_USER

    @Suppress("UNCHECKED_CAST")
    val explicitKinds = (mirror["kinds"] as List<AnnotationMirror>).map { annotation ->
        val name = annotation["name"] as String
        val params = annotation["params"] as List<AnnotationMirror>
        name to params.map { Parameter.fromAnnotation(it) }
    }
    val kinds = mutableMapOf(
        ACTIVITY_RESULT_CANCELED to DEFAULT_CANCELED_KIND,
        ACTIVITY_RESULT_OK to DEFAULT_OK_KIND
    )
    for ((name, params) in explicitKinds) {
        val code = when (name) {
            NAME_CANCELED -> ACTIVITY_RESULT_CANCELED
            NAME_OK -> ACTIVITY_RESULT_OK
            else -> nextResultId++
        }
        kinds[code] = ResultKind(code, name, params)
    }
    return kinds.values.sortedBy { it.code }
}
