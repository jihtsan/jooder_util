package com.example.proxy;

import java.lang.reflect.Proxy;

/**
 * @author Joo00der
 * @desc
 * @date 2021/4/9
 */
public class 测试了 {
    public static void main(String[] args) {
        ProxyForLanguage proxy = ProxyForLanguage.proxy(new PythonLanguage());
        proxy.poorLearn("床上");


    }
}
