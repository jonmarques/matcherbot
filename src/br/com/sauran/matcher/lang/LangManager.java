package br.com.sauran.matcher.lang;

import java.util.Map.Entry;
import java.util.TreeMap;

import br.com.sauran.matcher.utils.JSON;

public class LangManager {

	public static LangManager instance;
	private TreeMap<String, Language> langs;
	
	public LangManager() {
		instance = this;
		load();
	}

	private void load() {
		System.out.println("[Matcher] Carregando idiomas...");
		this.langs = new TreeMap<String, Language>(String.CASE_INSENSITIVE_ORDER);
		String[] files = new String[] {"pt-br", "en-us"};
		for (String file : files) {
			Language lang = (Language) JSON.jsonToObject(false, "/lang/" + file + ".json", Language.class);
			langs.put(file, lang);
			System.out.println("[Matcher] Idioma carregado: " + file);
		}
		System.out.println("[Matcher] Carregamento de idiomas finalizado.");
	}
	
	private TreeMap<String, Language> getLangs() {
		return langs;
	}
	
	public static String getKey(Language lang) {
		if (instance == null) new LangManager();
		for (Entry<String, Language> langs : instance.getLangs().entrySet()) {
			if (langs.getValue() == lang) return langs.getKey();
		}
		return null;
	}
	
	public static Language getLang(String string) {
		if (instance == null) new LangManager();
		return instance.getLangs().get(string);
	}
	
	public static TreeMap<String, Language> getLanguages() {
		if (instance == null) new LangManager();
		return instance.getLangs();
	}
	
}
