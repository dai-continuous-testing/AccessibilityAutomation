package com.experitest.accessibility;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class HtmlReportGenerator {

    private static final String header = "<h1>Accessibility Page analysis</h1>\n" +
            "<hr />\n" +
            "<p>Page name: <strong>${pageName}</strong></p>\n" +
            "<p><strong>Type of Tests</strong></p>\n" +
            "<p>&nbsp;</p>\n";
    private static final String sectionHeader = "<hr />\n" +
            "<p>Section 1</p>\n" +
            "<p><img src=\"localimage.png\" alt=\"page image\" /></p>\n";
    public static void generateReport(Page page, String pageName,  File folder) throws IOException {
        folder.mkdirs();
        StringBuffer buffer = new StringBuffer();
        buffer.append(header.replace("${pageName}", pageName));
        HashMap<Issue.Type, Integer> issuesCount = new HashMap<>();
        for(Element el: page.getElementsList()){
            for(Issue issue: el.getIssues()){
                if(!issuesCount.containsKey(issue.getType())){
                    issuesCount.put(issue.getType(), 0);
                }
                issuesCount.put(issue.getType(), issuesCount.get(issue.getType()) + 1);
            }
        }
        Table table = new Table(3);
        table.addRow("Test Type", "Description", "Status");
        HashSet<Issue.Type> typeSet = page.getValidationsSet();
        for(Issue.Type type: Issue.Type.values()){
            String testType = type.name();
            String description = type.getDescription();
            String status = null;
            if(!typeSet.contains(type)){
                status = "Wasn't executed";
            } else {
                if(issuesCount.containsKey(type)){
                    status = "" + issuesCount.get(type) + " issues";
                } else {
                    status = "No issues found";
                }
            }
            table.addRow(testType, description, status);
        }
        buffer.append(table);
        for(int i = 0; i < page.getSections().size(); i++){
            buffer.append("<hr/>\n");
            buffer.append("<p>Section ");
            buffer.append((i + 1));
            buffer.append("</p>\n");
            Section section = page.getSections().get(i);
            section.draw(new File(folder, "" + i + ".png"));
            buffer.append("<p><img style=\"max-height:500px;\" src=\"" + i + ".png\" alt=\"page image\" /></p>\n");

            Table elementTable = new Table(3);
            elementTable.addRow("Index", "Voice Over", "Issues");
            for(int j = 0; j < section.getElements().size(); j++){
                Element element = section.getElements().get(j);
                String index = "";
                String voiceOver = "";
                String issue = "";
                index = String.valueOf(j + 1);
                voiceOver = Arrays.toString(element.voiceOver());
                if(element.getIssues().size() > 0){
                    issue = element.getIssues().get(0).getMessage();
                }
                elementTable.addRow(index, voiceOver, issue);
                for(int ii = 1; ii < element.getIssues().size(); ii++){
                    table.addRow("", "", element.getIssues().get(ii).getMessage());
                }
            }
            buffer.append(elementTable.toString());
        }
        FileWriter writer = new FileWriter(new File(folder, "index.html"));
        writer.append("<html><body>\n");
        writer.append(buffer.toString());
        writer.append("</body></html>\n");
        writer.close();
    }

}
class Table {
    private int column;
    private ArrayList<String[]> rows = new ArrayList<>();
    public Table(int column){
        this.column = column;
    }
    public void addRow(String...array){
        if(array.length != column){
            throw new RuntimeException("Wrong input size, expected " + column);
        }
        rows.add(array);
    }
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("<table>\n<tbody>\n");
        for(String[] row: rows){
            buffer.append("<tr>\n");
            for(String value: row){
                buffer.append("<td>");
                buffer.append(value);
                buffer.append("</td>\n");
            }
            buffer.append("</tr>\n");
        }
        buffer.append("</tbody>\n</table>\n");
        return buffer.toString();
    }
}