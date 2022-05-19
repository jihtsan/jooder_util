package com.example.proxy;

/**
 * @author Joo00der
 * @desc
 * @date 2021/4/9
 */
public class ProxyForLanguage implements LanguageService{
    private LanguageService languageService;

    private ProxyForLanguage(LanguageService languageService) {
        this.languageService = languageService;
    }

    public static ProxyForLanguage proxy(LanguageService language) {
        return new ProxyForLanguage(language);
    }

    @Override
    public void poorLearn(String whereToLearn) {
        System.out.println("自定义调用前逻辑-------------");
        this.languageService.poorLearn(whereToLearn);
        System.out.println("自定义调用后逻辑-------------");
    }
}
