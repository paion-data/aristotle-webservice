package com.paiondata.aristotle.hanlp;

import edu.stanford.nlp.trees.TreeGraphNode;
import lombok.Data;

@Data
public class MainPart {

    public TreeGraphNode subject;

    public TreeGraphNode predicate;

    public TreeGraphNode object;

    public String result;

    public MainPart(TreeGraphNode subject, TreeGraphNode predicate, TreeGraphNode object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public MainPart(TreeGraphNode predicate) {
        this(null, predicate, null);
    }

    public MainPart() {
        result = "";
    }

    public void done() {
        result = predicate.toString("value");
        if (subject != null)
        {
            result = subject.toString("value") + result;
        }
        if (object != null)
        {
            result = result  + object.toString("value");
        }
    }

    public boolean isDone() {
        return result != null;
    }

    @Override
    public String toString() {
        if (result != null) return result;
        return "MainPart{" +
                "主语='" + subject + '\'' +
                ", 谓语='" + predicate + '\'' +
                ", 宾语='" + object + '\'' +
                '}';
    }
}