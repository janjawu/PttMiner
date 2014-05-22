package com.janja.pttminer.parser;

import java.io.IOException;
import java.net.URL;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public abstract class PttParser {
    public String url;

    protected abstract Object parseLogic(TagNode rootNode);

    public Object parseHtml(String url) throws IOException {
        this.url = url;
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode rootNode = htmlCleaner.clean(new URL(url));

        return parseLogic(rootNode);
    }
}
