package com.utils

import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.file.FileWriter
import cn.hutool.core.lang.Dict
import cn.hutool.core.util.StrUtil
import cn.hutool.extra.template.TemplateConfig
import cn.hutool.extra.template.TemplateUtil
import cn.hutool.poi.excel.ExcelUtil

/**
 * @desc
 * @author Joo00der
 * @date 2021/4/2
 */
object Dwd代码_文档生成 {

    val saveFilePath = "C:\\Users\\Joo00der\\Desktop\\数据清洗"
    val sourceFilePath = "C:\\Users\\Joo00der\\Desktop\\清洗重构项目模板"

    val savePath = "tmp\\dwd"

    fun 获取DWDPRE数据字典(dwdPreDescFilePath: String): List<Triple<String, String, String>> {
        val reader = FileUtil.getReader(dwdPreDescFilePath, "UTF-8")
        val readAll = reader.readLines()
        val result =
            readAll.filterIndexed { index, mutableList -> index >= 2 }
                .map { items ->
                    val it = items.split(",")
                    Triple(it[0], it[1], it[2])
                }.toList()
        return result
    }

    fun 获取DWD数据字典(dwdDescFilePath: String): List<Triple<String, String, String>> {
        val reader = ExcelUtil.getReader(dwdDescFilePath)
        val readAll = reader.read()
        val result =
            readAll.map { items -> Triple(items[1] as String, items[0] as String, items[2] as String) }.toList()
        return result
    }


    fun 生成赋值字段(sourceData: List<Triple<String, String, String>>): String {
        val fieldStr = StringBuffer()
        sourceData.filter { StrUtil.isNotBlank(it.first) && !StrUtil.equals("*", it.first) }.forEach {
            var fieldContent = ""
            when {
                StrUtil.isBlank(it.third) -> fieldContent += "${it.first} = //todo entData.${it.second} , \n"
                StrUtil.equals(it.third, "*") -> fieldContent += "${it.first} = entData.${it.second} , \n"
                StrUtil.equals(it.third, "time") -> fieldContent += "${it.first} = getCurrentTime , \n"
                else -> fieldContent += "${it.first} = ${it.third}(entData.${it.second}) , \n"
            }
            fieldStr.append(fieldContent)
        }
        return StringUtils.去除最后一个标志位(fieldStr.toString(), ",")
    }

    fun 生成字段实体类(
        sourceData: List<Triple<String, String, String>>,
        preMap: Map<String, Triple<String, String, String>>
    ): String {
        var fieldValueStr = StringBuffer()
        sourceData.forEach { it ->
            val preData = preMap.get(it.second)
            fieldValueStr.append("${it.first} : ${preData?.second} , \n")
        }
        return StringUtils.去除最后一个标志位(fieldValueStr.toString(), ",")
    }

    fun 生成对应的DWD文档(
        projectName: String, dwdTableName: String,
        sourceData: List<Triple<String, String, String>>,
        preMap: Map<String, Triple<String, String, String>>
    ) {
        val fw = StringBuffer()
        fw.append("DWD层:${dwdTableName} \n")
        fw.append("字段名称,字段类型,备注,字段含义 \n")
        sourceData.forEach {
            val preData = preMap.get(it.second)
            fw.append("${it.first},${preData?.second},${it.third},${preData?.third}, \r\n")
        }
        val file = "$saveFilePath\\$projectName\\$savePath\\${dwdTableName}文档.csv"
        FileUtils.重置文件夹(file, fw.toString())
    }

    fun Dwd清洗程序(
        projectName: String,
        projectNameCN: String,
        dwd_pre_table_name: String,
        dwd_pre_file_name: String,
        dwd_file_name: String,
        dwd_table_name: String,
        sourceData: List<Triple<String, String, String>>,
        writeProject: Boolean
    ) {
        val dwdAssignMentValue = 生成赋值字段(sourceData)
        val engine = TemplateUtil.createEngine(
            TemplateConfig(
                "${sourceFilePath}\\src\\main\\scala\\com\\icekredit\\bigdata\\job\\dwd",
                TemplateConfig.ResourceMode.FILE
            )
        )
        val template =
            engine.getTemplate("DWDFILE.vtl")
        val result = template.render(
            Dict.create()
                .set("projectName", projectName)
                .set("projectNameCN", projectNameCN)
                .set("dwd_file_name", dwd_file_name)
                .set("dwd_pre_table_name", dwd_pre_table_name)
                .set("dwd_pre_file_name", dwd_pre_file_name)
                .set("dwd_table_name", dwd_table_name)
                .set("dwd_fields_value", dwdAssignMentValue)
        )
        val file = "$saveFilePath\\$projectName\\$savePath\\${dwd_file_name}.scala"
        FileUtils.重置文件夹(file, result)
        if (writeProject) {
            val projectFile =
                "$saveFilePath\\$projectName\\src\\main\\scala\\com\\icekredit\\bigdata\\job\\dwd\\${dwd_file_name}.scala"
            FileUtils.重置文件夹(projectFile, result)
        }
    }

    fun DwdPO清洗程序(
        projectName: String,
        projectNameCN: String,
        dwd_file_name: String,
        sourceData: List<Triple<String, String, String>>,
        preSourceData: Map<String, Triple<String, String, String>>,
        writeProject: Boolean
    ) {
        val fieldContext = 生成字段实体类(sourceData, preSourceData)
        val engine = TemplateUtil.createEngine(
            TemplateConfig(
                "${sourceFilePath}\\src\\main\\scala\\com\\icekredit\\bigdata\\entity",
                TemplateConfig.ResourceMode.FILE
            )
        )
        val template =
            engine.getTemplate("DWDPOFILE.vtl")
        val result = template.render(
            Dict.create()
                .set("projectName", projectName)
                .set("projectNameCN", projectNameCN)
                .set("dwd_file_name", dwd_file_name)
                .set("dwd_fields", fieldContext)
        )
        val file = "$saveFilePath\\$projectName\\$savePath\\${dwd_file_name}PO.scala"
        FileUtils.重置文件夹(file, result)
        if (writeProject) {
            val projectFile =
                "$saveFilePath\\$projectName\\src\\main\\scala\\com\\icekredit\\bigdata\\entity\\${dwd_file_name}PO.scala"
            FileUtils.重置文件夹(projectFile, result)
        }
    }

    fun Dwd清洗命令生成程序(
        projectName: String,
        projectNameCN: String,
        dwd_file_name: String,
        dwd_table_name: String,
        writeProject: Boolean
    ) {
        val engine = TemplateUtil.createEngine(
            TemplateConfig(
                "${DwdPre代码_文档生成.sourceFilePath}\\bin\\dw_dw",
                TemplateConfig.ResourceMode.FILE
            )
        )
        val template =
            engine.getTemplate("DWDSHELL.sh")
        val result = template.render(
            Dict.create()
                .set("projectName", projectName)
                .set("projectNameCN", projectNameCN)
                .set("dwd_file_name", dwd_file_name)
        )
        val file = "$saveFilePath\\$projectName\\$savePath\\$dwd_table_name.sh"
        FileUtils.重置文件夹(file, result)
        if (writeProject) {
            val projectFile = "$saveFilePath\\$projectName\\bin\\dw_dw\\$dwd_table_name.sh"
            FileUtils.重置文件夹(projectFile, result)
        }
    }

    fun 整理生成的数据并生成文件(
        projectName: String,
        projectNameCN: String,
        dwd_pre_table_name: String,
        dwd_pre_file_name: String,
        dwd_table_name: String,
        dwd_file_name: String,
        preDocumentPath: String,
        documentPath: String
    ) {
        val preDocumentTriple = 获取DWDPRE数据字典(preDocumentPath)
        val documentTriple = 获取DWD数据字典(documentPath)
        val preDocMap = preDocumentTriple.map { it.first to it }.toMap()

        DwdPO清洗程序(projectName, projectNameCN, dwd_file_name, documentTriple, preDocMap, true)
        Dwd清洗程序(
            projectName,
            projectNameCN,
            dwd_pre_table_name,
            dwd_pre_file_name,
            dwd_file_name,
            dwd_table_name,
            documentTriple,
            true
        )
        生成对应的DWD文档(projectName, dwd_table_name, documentTriple, preDocMap)
        Dwd清洗命令生成程序(projectName, projectNameCN, dwd_file_name, dwd_table_name, true)
    }

}