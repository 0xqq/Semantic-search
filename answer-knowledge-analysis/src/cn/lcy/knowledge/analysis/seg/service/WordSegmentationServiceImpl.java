package cn.lcy.knowledge.analysis.seg.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.common.Term;

import cn.lcy.knowledge.analysis.config.Config;
import cn.lcy.knowledge.analysis.enums.OntologyClassEnum;
import cn.lcy.knowledge.analysis.sem.model.PolysemantNamedEntity;
import cn.lcy.knowledge.analysis.sem.model.Word;
import cn.lcy.knowledge.analysis.sem.model.WordSegmentResult;

public class WordSegmentationServiceImpl implements WordSegmentationServiceI {

	// 以省内存的方式读取 Answer_Dict 词典
	LinkedList<String> dictIndividualList = IOUtil.readLineListWithLessMemory(Config.individualDictPath);

	private volatile static WordSegmentationServiceI singleInstance;

	/**
	 * 私有化构造方法，实现单例模式
	 */
	private WordSegmentationServiceImpl() {
	}

	/**
	 * 获取单例
	 */
	public static WordSegmentationServiceI getInstance() {
		if (singleInstance == null) {
			synchronized (WordSegmentationServiceImpl.class) {
				if (singleInstance == null) {
					singleInstance = new WordSegmentationServiceImpl();
				}
			}
		}
		return singleInstance;
	}

	/**
	 * HanLP 分词以及命名实体识别
	 */
	@Override
	public WordSegmentResult wordSegmentation(String question) {
		// 命名实体
		List<PolysemantNamedEntity> polysemantNamedEntities = new ArrayList<PolysemantNamedEntity>();
		for (String dictRow : dictIndividualList) {
			// 覆盖模式插入
			CustomDictionary.insert(dictRow.split("_")[1], "n 2048");
		}

		// 本次分词主要为命名实体识别
		List<Term> terms = HanLP.segment(question);

		for (String dictRow : dictIndividualList) {

			String[] fieldsDict = dictRow.split("_");
			String dictIndividualUUID = fieldsDict[0]; // UUID
			String dictIndividualName = fieldsDict[1]; // 实体名
			String dictPolysemantExplain = fieldsDict[2]; // 歧义说明
			String dictIndividualURL = fieldsDict[3]; // 实体百科页面URL
			String dictIsAliasesWrite = fieldsDict[4]; // 是否是本名
			int dictIndividualClass = Integer.parseInt(fieldsDict[5]); // 实体所属类型

			int id = 1;
			for (Term term : terms) {
				if (term.word.equals(dictIndividualName)) {

					PolysemantNamedEntity polysemantNamedEntitiy = new PolysemantNamedEntity();

					polysemantNamedEntitiy.setUUID(dictIndividualUUID);
					polysemantNamedEntitiy.setEntityName(dictIndividualName);
					polysemantNamedEntitiy.setPolysemantExplain(dictPolysemantExplain);
					polysemantNamedEntitiy.setEntityUrl(dictIndividualURL);
					polysemantNamedEntitiy.setIsAliases(dictIsAliasesWrite);

					// 默认均为未激活状态
					polysemantNamedEntitiy.setActive(false);

					//	实体的类别
					if (dictIndividualClass == OntologyClassEnum.CHARACTER.getIndex()) {

						polysemantNamedEntitiy.setOntClass(OntologyClassEnum.CHARACTER.getName());

					} else if (dictIndividualClass == OntologyClassEnum.MOVIE.getIndex()) {
						polysemantNamedEntitiy.setOntClass(OntologyClassEnum.MOVIE.getName());
					} else if (dictIndividualClass == OntologyClassEnum.MUSIC.getIndex()) {
						polysemantNamedEntitiy.setOntClass(OntologyClassEnum.MUSIC.getName());
					} else if (dictIndividualClass == OntologyClassEnum.ANIMATION.getIndex()) {
						polysemantNamedEntitiy.setOntClass(OntologyClassEnum.ANIMATION.getName());
					} else if (dictIndividualClass == OntologyClassEnum.CARICATURE.getIndex()) {
						polysemantNamedEntitiy.setOntClass(OntologyClassEnum.CARICATURE.getName());
					} else if (dictIndividualClass == OntologyClassEnum.AREA.getIndex()) {
						polysemantNamedEntitiy.setOntClass(OntologyClassEnum.AREA.getName());
					} else if (dictIndividualClass == OntologyClassEnum.ACADEMY.getIndex()) {
						polysemantNamedEntitiy.setOntClass(OntologyClassEnum.ACADEMY.getName());
					} else if (dictIndividualClass == OntologyClassEnum.COMPANY.getIndex()) {
						polysemantNamedEntitiy.setOntClass(OntologyClassEnum.COMPANY.getName());
					} else if (dictIndividualClass == OntologyClassEnum.OTHERS.getIndex()) {
						polysemantNamedEntitiy.setOntClass(OntologyClassEnum.OTHERS.getName());
					}
					polysemantNamedEntitiy.setPosition(id);
					polysemantNamedEntities.add(polysemantNamedEntitiy);
				}
				++id;
			}
		}
		// 加载用户词典后的分词
		List<Term> termList = HanLP.segment(question);

		int index = 1;
		List<Word> words = new ArrayList<Word>();
		for (Term term : terms) {
			Word word = new Word();
			word.setPosition(index);
			word.setName(term.word);
			word.setCpostag(term.nature.toString());
			word.setPostag(term.nature.toString());
			List<PolysemantNamedEntity> wordPolysemantNamedEntities = new ArrayList<PolysemantNamedEntity>();
			for (PolysemantNamedEntity polysemantNamedEntity : polysemantNamedEntities) {
				if (polysemantNamedEntity.getPosition() == index) {
					wordPolysemantNamedEntities.add(polysemantNamedEntity);
				}
			}
			word.setPolysemantNamedEntities(wordPolysemantNamedEntities);
			words.add(word);
			++index;
		}
		// 分词结果
		WordSegmentResult wordSegmentResult = new WordSegmentResult();
		wordSegmentResult.setTerms(termList);
		wordSegmentResult.setPolysemantEntities(polysemantNamedEntities);
		wordSegmentResult.setWords(words);
		return wordSegmentResult;
	}
}
