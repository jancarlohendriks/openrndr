package org.openrndr.internal.gl3

import org.openrndr.draw.*

fun array(item: VertexElement): String = if (item.arraySize == 1) "" else "[${item.arraySize}]"

fun structureFromShadeStyle(shadeStyle: ShadeStyle?, vertexFormats: List<VertexFormat>, instanceAttributeFormats: List<VertexFormat>): ShadeStructure {
    return ShadeStructure().apply {
        if (shadeStyle != null) {
            vertexTransform = shadeStyle.vertexTransform
            fragmentTransform = shadeStyle.fragmentTransform
            vertexPreamble = shadeStyle.vertexPreamble
            fragmentPreamble = shadeStyle.fragmentPreamble
            outputs = shadeStyle.outputs.map { "layout(location = ${it.value}) out vec4 o_${it.key};\n" }.joinToString("")
            uniforms = shadeStyle.parameters.map { "uniform ${mapType(it.value)} p_${it.key};\n" }.joinToString("")
        }
        varyingOut = vertexFormats.flatMap { it.items }.joinToString("") { "${it.type.glslVaryingQualifier}out ${it.type.glslType} va_${it.attribute}${array(it)};\n" } +
                instanceAttributeFormats.flatMap { it.items }.joinToString("") { "${it.type.glslVaryingQualifier}out ${it.type.glslType} vi_${it.attribute}${array(it)};\n" }
        varyingIn = vertexFormats.flatMap { it.items }.joinToString("") { "${it.type.glslVaryingQualifier}in ${it.type.glslType} va_${it.attribute}${array(it)};\n" } +
                instanceAttributeFormats.flatMap { it.items }.joinToString("") { "${it.type.glslVaryingQualifier}in ${it.type.glslType} vi_${it.attribute}${array(it)};\n" }
        varyingBridge = vertexFormats.flatMap { it.items }.joinToString("") { "    va_${it.attribute} = a_${it.attribute};\n" } +
                instanceAttributeFormats.flatMap { it.items }.joinToString("") { "vi_${it.attribute} = i_${it.attribute};\n" }
        attributes = vertexFormats.flatMap { it.items }.joinToString("") { "in ${it.type.glslType} a_${it.attribute}${array(it)};\n" } +
                instanceAttributeFormats.flatMap { it.items }.joinToString("") { "in ${it.type.glslType} i_${it.attribute}${array(it)};\n" }

        suppressDefaultOutput = shadeStyle?.suppressDefaultOutput ?: false
    }
}

private fun mapType(type: String): String {
    val tokens = type.split(",")
    val arraySize = tokens.getOrNull(1)
    return when (tokens[0]) {
        "Int", "int" -> "int"
        "Matrix33" -> "mat3"
        "Matrix44" -> "mat4${if (arraySize!=null) "[$arraySize]" else ""}"
        "Float", "float" -> "float"
        "Vector2" -> "vec2"
        "Vector3" -> "vec3"
        "Vector4" -> "vec4"
        "ColorRGBa" -> "vec4"
        "BufferTexture" -> "samplerBuffer"
        "ColorBuffer" -> "sampler2D"
        "ColorBuffer_UINT" -> "usampler2D"
        "ColorBuffer_SINT" -> "isampler2D"
        "DepthBuffer" -> "sampler2D"
        "Cubemap" -> "samplerCube"
        "ArrayTexture" -> "sampler2DArray"
        "ArrayTexture_UINT" -> "usampler2DArray"
        "ArrayTexture_SINT" -> "isampler2DArray"
        else -> throw RuntimeException("unsupported type $type")
    }
}

private val VertexElementType.glslType: String
    get() {
        return when (this) {
            VertexElementType.INT8, VertexElementType.INT16, VertexElementType.INT32 -> "int"
            VertexElementType.UINT8, VertexElementType.UINT16, VertexElementType.UINT32 -> "uint"
            VertexElementType.VECTOR2_UINT8, VertexElementType.VECTOR2_UINT16, VertexElementType.VECTOR2_UINT32 -> "uvec2"
            VertexElementType.VECTOR2_INT8, VertexElementType.VECTOR2_INT16, VertexElementType.VECTOR2_INT32 -> "ivec2"
            VertexElementType.VECTOR3_UINT8, VertexElementType.VECTOR3_UINT16, VertexElementType.VECTOR3_UINT32 -> "uvec3"
            VertexElementType.VECTOR3_INT8, VertexElementType.VECTOR3_INT16, VertexElementType.VECTOR3_INT32 -> "ivec3"
            VertexElementType.VECTOR4_UINT8, VertexElementType.VECTOR4_UINT16, VertexElementType.VECTOR4_UINT32 -> "uvec4"
            VertexElementType.VECTOR4_INT8, VertexElementType.VECTOR4_INT16, VertexElementType.VECTOR4_INT32 -> "ivec4"
            VertexElementType.FLOAT32 -> "float"
            VertexElementType.VECTOR2_FLOAT32 -> "vec2"
            VertexElementType.VECTOR3_FLOAT32 -> "vec3"
            VertexElementType.VECTOR4_FLOAT32 -> "vec4"
            VertexElementType.MATRIX22_FLOAT32 -> "mat2"
            VertexElementType.MATRIX33_FLOAT32 -> "mat3"
            VertexElementType.MATRIX44_FLOAT32 -> "mat4"
        }
    }

private val VertexElementType.glslVaryingQualifier: String
    get() {
        return when (this) {
            VertexElementType.INT8, VertexElementType.INT16, VertexElementType.INT32 -> "flat "
            VertexElementType.UINT8, VertexElementType.UINT16, VertexElementType.UINT32 -> "flat "
            VertexElementType.VECTOR2_UINT8, VertexElementType.VECTOR2_UINT16, VertexElementType.VECTOR2_UINT32 -> "flat "
            VertexElementType.VECTOR2_INT8, VertexElementType.VECTOR2_INT16, VertexElementType.VECTOR2_INT32 -> "flat "
            VertexElementType.VECTOR3_UINT8, VertexElementType.VECTOR3_UINT16, VertexElementType.VECTOR3_UINT32 -> "flat "
            VertexElementType.VECTOR3_INT8, VertexElementType.VECTOR3_INT16, VertexElementType.VECTOR3_INT32 -> "flat "
            VertexElementType.VECTOR4_UINT8, VertexElementType.VECTOR4_UINT16, VertexElementType.VECTOR4_UINT32 -> "flat "
            VertexElementType.VECTOR4_INT8, VertexElementType.VECTOR4_INT16, VertexElementType.VECTOR4_INT32 -> "flat "
            else -> ""
        }
    }
