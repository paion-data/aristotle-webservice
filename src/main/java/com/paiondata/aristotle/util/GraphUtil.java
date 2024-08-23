package com.paiondata.aristotle.util;

import edu.stanford.nlp.trees.TreeGraphNode;

import java.util.Objects;

public class GraphUtil {

    public static String getNodeValue(TreeGraphNode treeGraphNode){
        if (Objects.nonNull(treeGraphNode))
            return treeGraphNode.toString("value");
        return null;
    }


}
