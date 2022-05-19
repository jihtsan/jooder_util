package com.utils

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.collection.ListUtil
import cn.hutool.db.nosql.mongo.MongoFactory
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.JSONPath
import com.alibaba.fastjson.serializer.SerializerFeature
import cn.hutool.poi.excel.ExcelUtil

import cn.hutool.poi.excel.ExcelReader
import java.security.CodeSource


object Mongo爬虫数据整理 {

    fun 检查JSON数据覆盖全面(spiderDb: String, spiderCollection: String, checkCount: Int, keyDataPath: String): JSONObject {
        val mongoConnect = MongoFactory.getDS("master").getCollection(spiderDb, spiderCollection)
        val checkData = mongoConnect.find().limit(checkCount)
        val cursor = checkData.iterator()
        val jsonReuslt = JSONObject(true)
        while (cursor.hasNext()) {
            val data = cursor.next()
            val jsonData = JSON.parseObject(JSON.toJSONString(data))
            val getJson = JSONUtils.getJSONObjectWithPath(jsonData, keyDataPath)
            jsonReuslt.putAll(getJson)
        }
        return jsonReuslt
    }

    fun 获取爬虫数据字典(filePath: String): Map<String, String> {
        val reader = ExcelUtil.getReader(filePath)
        val readAll = reader.read()
        val result = readAll.map { items -> items[1] as String to items[0] as String }.toMap()
        return result
    }


    fun 对比文档与爬虫源数据(targetList: List<String>, source: List<String>) {
        val addTarget = ListUtil.toCopyOnWriteArrayList(targetList)
        val addSource = ListUtil.toCopyOnWriteArrayList(source)
        val addTarget1 = ListUtil.toCopyOnWriteArrayList(targetList)
        addTarget.removeAll(addSource)
        addSource.removeAll(addTarget1)
        if (CollUtil.isEmpty(addTarget) && CollUtil.isEmpty(addSource)) println("数据完全一致!")
        if (!CollUtil.isEmpty(addTarget)) println("文档数据缺少:$addTarget")
        if (!CollUtil.isEmpty(addSource)) println("数据源数据缺少:$addSource")
    }

    fun 输出结果(
        spiderDb: String, spiderCollection: String,
        checkCount: Int, keyDataPath: String,
        documentPath: String
    ) {
        val result = 检查JSON数据覆盖全面(spiderDb, spiderCollection, checkCount, keyDataPath)
        println("----------------检查数据源----------------")
        println(JSON.toJSONString(result, SerializerFeature.PrettyFormat))
        println("---------end----检查数据源----------------")
        val crawler = 获取爬虫数据字典(documentPath)
        对比文档与爬虫源数据(result.keys.toList(), crawler.keys.toList())
    }

}