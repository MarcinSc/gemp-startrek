package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable;

public class SystemOutTagTextHandler implements TagTextHandler {
    @Override
    public void processTag(String tag) {
        System.out.println("Tag: " + tag);
    }

    @Override
    public void processText(String text) {
        System.out.println("Text: " + text);
    }
}
