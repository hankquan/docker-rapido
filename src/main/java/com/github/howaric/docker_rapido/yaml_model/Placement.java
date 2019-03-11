package com.github.howaric.docker_rapido.yaml_model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Placement {

    private List<String> constraints;
    private static final String NODE_NAME = "node.name";
    private static final String NODE_LABELS = "node.labels";

    public List<String> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<String> constraints) {
        this.constraints = constraints;
    }

    // "constraints" : [ "node.name == demo-node1", "node.labels.site == Ki" ]
    public List<Node> targetNodes(Map<String, Node> nodes) {
        List<Node> result = new ArrayList<>();
        for (String constraint : constraints) {
            String[] split = constraint.split("==");
            String key = split[0].trim();// node.name
            String value = split[1].trim();// node name
            if (key.equals(NODE_NAME)) {
                if (nodes.containsKey(value)) {
                    result.add(nodes.get(value));
                }
            } else if (key.startsWith(NODE_LABELS)) {
                String label = key.substring(key.indexOf(NODE_LABELS) + NODE_LABELS.length() + 1);
                Collection<Node> values = nodes.values();
                for (Node node : values) {
                    if (node.hasLabel(label, value)) {
                        result.add(node);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Placement [constraints=").append(constraints).append("]");
        return builder.toString();
    }

}
