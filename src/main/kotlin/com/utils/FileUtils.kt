package com.utils

import cn.hutool.core.io.FileUtil
import java.io.FileWriter

/**
 * @desc
 * @author Joo00der
 * @date 2021/4/5
 */
object FileUtils {

    fun 重置文件夹(filePath: String,content:String) {
        FileUtil.del(filePath)
        FileUtil.touch(filePath)
        val fw = FileWriter(filePath)
        fw.write(content)
        fw.flush()
        fw.close()
    }
}