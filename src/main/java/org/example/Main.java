package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dnl.utils.text.table.TextTable;
import org.apache.commons.collections4.CollectionUtils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Main {


    public static void main(String[] args) throws Exception {
        String repo = args.length > 0 ? args[0] : "metersphere/metersphere";
        List<Object> pullRequestComments = new ArrayList<>();
        String currentMonth = new SimpleDateFormat("yyyy-MM").format(Calendar.getInstance().getTime());
        int page = 1;
        do {
            ObjectMapper objectMapper = new ObjectMapper();
            List<LinkedHashMap> list = objectMapper.readValue(new URL("https://api.github.com/repos/" + repo + "/pulls/comments?since=" + currentMonth + "-01T00:00:00Z&per_page=100&page=" + page), new TypeReference<List>() {
            });
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            pullRequestComments.addAll(list);
            page++;
        } while (true);
        System.out.println("________________________________");
        System.out.println("| " + currentMonth + " 月份GitHub PR 审查汇总|");
        System.out.println("________________________________");
        if (CollectionUtils.isNotEmpty(pullRequestComments)) {
            Map<String, Long> hm = pullRequestComments.stream().collect(Collectors.groupingBy(item -> ((LinkedHashMap) ((LinkedHashMap) item).get("user")).get("login").toString(), Collectors.counting()));
            List<String[]> lines = new ArrayList<>();
            hm.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList()).forEach(item -> {
                lines.add(new String[]{item.getKey(), String.valueOf(item.getValue())});
            });
            Collections.reverse(lines);
            lines.add(new String[]{"Total", String.valueOf(pullRequestComments.size())});
            TextTable tt = new TextTable(new String[]{"GitHub User", "Count"}, lines.toArray(new String[][]{}));
            tt.printTable();
        } else {
            System.out.println("NO DATA.");
        }
    }
}