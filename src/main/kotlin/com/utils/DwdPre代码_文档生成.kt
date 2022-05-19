package com.utils

import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.file.FileWriter
import cn.hutool.core.lang.Dict
import cn.hutool.core.util.StrUtil
import cn.hutool.extra.template.TemplateConfig
import cn.hutool.extra.template.TemplateUtil
import cn.hutool.poi.excel.ExcelUtil
import java.lang.Exception

/**
 * @desc
 * @author Joo00der
 * @date 2021/4/2
 */
object DwdPre代码_文档生成 {

    val saveFilePath = "C:\\Users\\Joo00der\\Desktop\\数据清洗"
    val sourceFilePath = "C:\\Users\\Joo00der\\Desktop\\清洗重构项目模板"

    val savePath = "tmp\\dwd_pre"

    fun 获取爬虫数据字典(descFilePath: String): List<Triple<String, String, String>?> {

        val reader = ExcelUtil.getReader(descFilePath)
        val readAll = reader.read()
        val result =
            readAll.mapIndexed { index, items ->
                try {
                    Triple(items[1] as String, items[0] as String, items[2] as String)
                }catch (e:Exception){
                    println(e)
                    println("第${index}行,${items.toString()}")
                    Triple("","","")
                }
            }
        return result
    }

    fun 生成字段代码(sourceData: List<Triple<String, String, String>?>): String {
        val fieldStr = StringBuffer()
        sourceData.forEach { it ->
            fieldStr.append(StrUtil.toSymbolCase(it?.first, '_'))
                .append(" : ").append(it?.third).append(",").append("\r\n")
        }
        return fieldStr.toString()
    }

    fun 生成对应的代码获取逻辑(sourceData: List<Triple<String, String, String>?>): String {
        val fieldValueStr = StringBuffer()
        sourceData.forEach {
            fieldValueStr.append(StrUtil.toSymbolCase(it?.first, '_'))
                .append(" = ")
            if (StrUtil.equals(it?.third, "String"))
                fieldValueStr.append("JSONUtils.getStringWithPath(data , ").append("\"$.").append(it?.first)
                    .append("\"),").append("\r\n")
            else if (StrUtil.contains(it?.third, "Array"))
                fieldValueStr.append("getArrayStringWithJSONArrayStr(JSONUtils.getJSONArrayWithPath(data , ")
                    .append("\"$.").append(it?.first)
                    .append("\")),").append("\r\n")
            else print("-----ERROR------ 添加新的枚举")
        }
        return fieldValueStr.toString()
    }

    fun 生成对应的DWDPRE文档(
        projectName: String, dwdPreTableName: String,
        sourceData: List<Triple<String, String, String>?>
    ) {

        val fw = StringBuffer()
        fw.append("DWD层:${dwdPreTableName} \n")
        fw.append("字段名称,字段类型,字段含义 \n")
        fw.append("trace_id,String,清洗数据唯一标识UUID \r\n")
        fw.append("source_id,String,爬虫数据唯一标识 \r\n")
        fw.append("source_time,String,数据源更新时间 \r\n")
        fw.append("source_type,String,数据类型 \r\n")
        sourceData.forEach { it ->
            fw.append("${StrUtil.toSymbolCase(it?.first, '_')},${it?.third},${it?.second} \r\n")
        }
        fw.append("create_time,String,创建时间 \r\n")
        fw.append("ymd,String,时间分区 \r\n")

        val file = "$saveFilePath\\$projectName\\${savePath}\\${dwdPreTableName}文档.csv"
        FileUtils.重置文件夹(file, fw.toString())
    }

    fun DwdPre清洗程序(
        projectName: String,
        projectNameCN: String,
        dwd_pre_table_name: String,
        dwd_pre_file_name: String,
        source_table: String,
        writeProject: Boolean
    ) {
        val engine = TemplateUtil.createEngine(
            TemplateConfig(
                "$sourceFilePath\\src\\main\\scala\\com\\icekredit\\bigdata\\job\\dwd_pre",
                TemplateConfig.ResourceMode.FILE
            )
        )
        val template =
            engine.getTemplate("DWDPREFILE.vtl")
        val result = template.render(
            Dict.create()
                .set("projectName", projectName)
                .set("projectNameCN", projectNameCN)
                .set("dwd_pre_file_name", dwd_pre_file_name)
                .set("source_table", source_table)
                .set("dwd_pre_table_name", dwd_pre_table_name)
        )
        val file = "$saveFilePath\\$projectName\\${savePath}\\$dwd_pre_file_name.scala"
        FileUtils.重置文件夹(file, result)
        if (writeProject) {
            val projectFile =
                "${Dwd代码_文档生成.saveFilePath}\\$projectName\\src\\main\\scala\\com\\icekredit\\bigdata\\job\\dwd_pre\\$dwd_pre_file_name.scala"
            FileUtils.重置文件夹(projectFile, result)
        }
    }

    fun DwdPrePO清洗程序(
        projectName: String,
        projectNameCN: String,
        dwd_pre_table_name: String,
        dwd_pre_file_name: String,
        source_table: String,
        source_data_path: String,
        sourceData: List<Triple<String, String, String>?>,
        writeProject: Boolean
    ) {
        val fieldContext = 生成字段代码(sourceData)
        val fieldValueContext = 生成对应的代码获取逻辑(sourceData)
        val engine = TemplateUtil.createEngine(
            TemplateConfig(
                "$sourceFilePath\\src\\main\\scala\\com\\icekredit\\bigdata\\entity",
                TemplateConfig.ResourceMode.FILE
            )
        )
        val template =
            engine.getTemplate("DWDPREPOFILE.vtl")
        val result = template.render(
            Dict.create()
                .set("projectName", projectName)
                .set("projectNameCN", projectNameCN)
                .set("dwd_pre_file_name", dwd_pre_file_name)
                .set("source_table", source_table)
                .set("source_data_path", source_data_path)
                .set("dwd_pre_table_name", dwd_pre_table_name)
                .set("dwd_pre_fields", fieldContext)
                .set("dwd_pre_fields_value", fieldValueContext)
        )
        val file = "$saveFilePath\\$projectName\\${savePath}\\${dwd_pre_file_name}PO.scala"
        FileUtils.重置文件夹(file, result)
        if (writeProject) {
            val projectFile =
                "$saveFilePath\\$projectName\\src\\main\\scala\\com\\icekredit\\bigdata\\entity\\${dwd_pre_file_name}PO.scala"
            FileUtils.重置文件夹(projectFile, result)
        }
    }

    fun DwdPre清洗命令生成程序(
        projectName: String,
        projectNameCN: String,
        dwd_pre_file_name: String,
        dwd_pre_table_name: String,
        writeProject: Boolean
    ) {
        val engine = TemplateUtil.createEngine(
            TemplateConfig(
                "$sourceFilePath\\bin\\ods_dw",
                TemplateConfig.ResourceMode.FILE
            )
        )
        val template =
            engine.getTemplate("DWDPRESHELL.sh")
        val result = template.render(
            Dict.create()
                .set("projectName", projectName)
                .set("projectNameCN", projectNameCN)
                .set("dwd_pre_file_name", dwd_pre_file_name)
        )
        val file = "$saveFilePath\\$projectName\\${savePath}\\${dwd_pre_table_name}.sh"
        FileUtils.重置文件夹(file, result)
        if (writeProject) {
            val projectFile =
                "$saveFilePath\\$projectName\\bin\\ods_dw\\${dwd_pre_table_name}.sh"
            FileUtils.重置文件夹(projectFile, result)
        }
    }


    fun 整理生成的数据并生成文件(
        projectName: String,
        projectNameCN: String,
        dwd_pre_table_name: String,
        dwd_pre_file_name: String,
        source_table: String,
        source_data_path: String,
        documentPath: String,
        writeProject: Boolean = true
    ) {
        val documentTriple = 获取爬虫数据字典(documentPath)
        DwdPre清洗程序(projectName, projectNameCN, dwd_pre_table_name, dwd_pre_file_name, source_table, writeProject)
        DwdPrePO清洗程序(
            projectName,
            projectNameCN,
            dwd_pre_table_name,
            dwd_pre_file_name,
            source_table,
            source_data_path,
            documentTriple,
            writeProject
        )
        生成对应的DWDPRE文档(projectName, dwd_pre_table_name, documentTriple)
        DwdPre清洗命令生成程序(projectName, projectNameCN, dwd_pre_file_name, dwd_pre_table_name, writeProject)
    }

}