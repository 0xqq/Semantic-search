/*package cn.lcy.answer.demo;

 * <summary></summary>
 * <author>He Han</author>
 * <email>me@hankcs.com</email>
 * <create-date>16/3/14 AM11:49</create-date>
 *
 * <copyright file="DemoCustomNature.java" company="码农场">
 * Copyright (c) 2008-2016, 码农场. All Right Reserved, http://www.hankcs.com/
 * This source is subject to Hankcs. Please contact Hankcs to get more information.
 * </copyright>
 

import java.util.List;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.corpus.util.CustomNatureUtility;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.utility.LexiconUtility;

*//**
 * 演示自定义词性,以及往词典中插入自定义词性的词语
 * !!!由于采用了反射技术,用户需对本地环境的兼容性和稳定性负责!!!
 *
 * @author hankcs
 *//*
public class DemoCustomNature
{
    @SuppressWarnings("incomplete-switch")
	public static void main(String[] args)
    {
        // 对于系统中已有的词性,可以直接获取
        Nature pcNature = Nature.fromString("n");
        System.out.println(pcNature);
        // 此时系统中没有"电脑品牌"这个词性
        pcNature = Nature.fromString("电脑品牌");
        System.out.println(pcNature);
        // 我们可以动态添加一个
        pcNature = Nature.create("pc");
        System.out.println(pcNature);
        // 可以将它赋予到某个词语
        LexiconUtility.setAttribute("苹果", pcNature);
        // 或者
        LexiconUtility.setAttribute("苹果", "pc 1000");
        // 它们将在分词结果中生效
        List<Term> termList = HanLP.segment("苹果电脑可以运行开源阿尔法狗代码吗");
        System.out.println(termList);
        for (Term term : termList)
        {
            if (term.nature == pcNature)
                System.out.printf("找到了 [%s] : %s\n", pcNature, term.word);
        }
        // 还可以直接插入到用户词典
        CustomDictionary.insert("阿尔法狗", "科技名词 1024");
        termList = HanLP.segment("苹果电脑可以运行开源阿尔法狗代码吗");
        System.out.println(termList);
        // 如果使用了动态词性之后任何类使用了switch(nature)语句,必须注册每个类:
        CustomNatureUtility.registerSwitchClass(DemoCustomNature.class);
        for (Term term : termList)
        {
            switch (term.nature)
            {
                case n:
                    System.out.printf("找到了 [%s] : %s\n", "名词", term.word);
            }
        }
    }
}
*/