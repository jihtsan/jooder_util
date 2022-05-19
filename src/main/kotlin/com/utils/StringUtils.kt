package com.utils

import cn.hutool.core.util.StrUtil

/**
 * @desc
 * @author Joo00der
 * @date 2021/4/5
 */
object StringUtils {
    @JvmStatic
    fun main(args: Array<String>) {
        println(文件名生成("dwd_pre_table_name"))
    }

    fun 去除最后一个标志位(target: String, obj: String): String {
        if (StrUtil.isBlank(target)) return target
        if (!StrUtil.contains(target, obj)) return target

        return StrUtil.sub(target, 0, target.lastIndexOf(obj))
    }

    fun 文件名生成(table_name: String): String {
        return table_name.substring(0, 1).toUpperCase() + StrUtil.toCamelCase(table_name.substring(1))
    }
}