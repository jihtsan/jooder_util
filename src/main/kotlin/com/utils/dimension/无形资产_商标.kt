package com.utils.dimension

import com.utils.DwdPre代码_文档生成
import com.utils.Dwd代码_文档生成
import com.utils.StringUtils.文件名生成
import com.utils.初始化项目目录

/**
 * @desc
 * @author Joo00der
 * @date 2021/4/2
 */
object 无形资产_商标 {
    @JvmStatic
    fun main(args: Array<String>) {
        val crawlerDocument = "D:\\数据清洗文档\\无形资产-商标-爬虫字典.xlsx"
        val document = "D:\\数据清洗文档\\无形资产-商标-DWD字典.xlsx"
        val collectionName = "wxzc_patent_data_20201118"
        val dbName = "common_spider_online"
        val keyDataPath = "$.data"
//        Mongo爬虫数据整理.输出结果(dbName, collectionName, 10000, keyDataPath, crawlerDocument)
        val projectName = "intangible-assets-trademark"
        val projectNameCN = "无形资产-商标-清洗"
        val dwd_pre_table_name = "dwd_pre_intangible_assets_trademark_c02"
        val dwd_pre_file_name = 文件名生成(dwd_pre_table_name)
        val dwd_table_name = "dwd_intangible_assets_trademark_c02"
        val dwd_file_name = 文件名生成(dwd_table_name)
        val source_table = "ods.bc_api_mongo_data"
        初始化项目目录.初始化项目(projectName, projectNameCN)
        DwdPre代码_文档生成.整理生成的数据并生成文件(
            projectName, projectNameCN, dwd_pre_table_name,
            dwd_pre_file_name, source_table, keyDataPath, crawlerDocument
        )
        val preDocument =
            "${DwdPre代码_文档生成.saveFilePath}\\$projectName\\${DwdPre代码_文档生成.savePath}\\${dwd_pre_table_name}文档.csv"
        Dwd代码_文档生成.整理生成的数据并生成文件(
            projectName, projectNameCN,
            dwd_pre_table_name, dwd_pre_file_name, dwd_table_name, dwd_file_name, preDocument, document
        )
    }
}