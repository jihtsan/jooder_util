package com.utils

import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.file.FileWriter
import cn.hutool.core.lang.Dict
import cn.hutool.extra.template.TemplateConfig
import cn.hutool.extra.template.TemplateUtil
import java.nio.file.Path
import java.time.DayOfWeek.of
import java.util.*

/**
 * @desc
 * @author Joo00der
 * @date 2021/4/5
 */
object 初始化项目目录 {

    val saveFilePath = "C:\\Users\\Joo00der\\Desktop\\数据清洗"
    val sourceFilePath = "C:\\Users\\Joo00der\\Desktop\\清洗重构项目模板"

    val touchFilePath = Arrays.asList(
        "bin\\ods_dw", "bin\\dw_dw", "bin\\dw_dm", "conf", "script",
        "src\\main\\scala\\com\\icekredit\\bigdata\\entity",
        "src\\main\\scala\\com\\icekredit\\bigdata\\job\\dwd_pre",
        "src\\main\\scala\\com\\icekredit\\bigdata\\job\\dwd",
        "src\\main\\scala\\com\\icekredit\\bigdata\\job\\dim"
    )

    val copyFilePath = Arrays.asList(
        "assembly\\assembly.xml",
        "src\\main\\scala\\com\\icekredit\\bigdata\\SparkUtils.scala",
        "conf\\court.csv"
    )

    fun 初始化项目(projectName: String, projectNameCN: String) {
        for (path in touchFilePath) {
            FileUtil.del("$saveFilePath\\$projectName\\$path")
            FileUtil.mkdir("$saveFilePath\\$projectName\\$path")
        }
        for (path in copyFilePath) {
            FileUtil.del("$saveFilePath\\$projectName\\$path")
            FileUtil.touch("$saveFilePath\\$projectName\\$path")
            FileUtil.copy(
                "$sourceFilePath\\$path",
                "$saveFilePath\\$projectName\\$path", true
            )
        }
        val engine = TemplateUtil.createEngine(
            TemplateConfig(
                "$sourceFilePath",
                TemplateConfig.ResourceMode.FILE
            )
        )
        var template =
            engine.getTemplate(".gitignore")
        var result = template.render(
            Dict.create()
                .set("projectName", projectName)
        )
        var file = "$saveFilePath\\$projectName\\.gitigonre"
        FileUtils.重置文件夹(file,result)

        template =
            engine.getTemplate("pom.xml")
        result = template.render(
            Dict.create()
                .set("projectName", projectName)
        )
        file = "$saveFilePath\\$projectName\\pom.xml"
        FileUtils.重置文件夹(file,result)

        template =
            engine.getTemplate("README.md")
        result = template.render(
            Dict.create()
                .set("projectNameCN", projectNameCN)
        )
        file = "$saveFilePath\\$projectName\\README.md"
        FileUtils.重置文件夹(file,result)
    }


}
