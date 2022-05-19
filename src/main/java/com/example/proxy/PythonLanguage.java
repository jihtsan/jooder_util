package com.example.proxy;

/**
 * @author Joo00der
 * @desc
 * @date 2021/4/9
 */
public class PythonLanguage implements LanguageService{
    @Override
    public void poorLearn(String whereToLearn) {
        System.out.println("每天在"+whereToLearn+"学习一会儿!");
    }
}
