/*
 *@author ChenCheng
 *@date 2019/11/14
 */
package com.hzucc.MyUtil.entity;

public class Problem {
    private int problemId;
    private String problemName;
    private String problemContent;
    private TimeLimit timeLimit;
    private MemoryLimit memoryLimit;

    @Override
    public String toString() {
        return "Problem{" +
                "problemId=" + problemId +
                ", problemName='" + problemName + '\'' +
                ", problemContent='" + problemContent + '\'' +
                ", timeLimit=" + timeLimit +
                ", memoryLimit=" + memoryLimit +
                '}';
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getProblemContent() {
        return problemContent;
    }

    public void setProblemContent(String problemContent) {
        this.problemContent = problemContent;
    }

    public TimeLimit getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(TimeLimit timeLimit) {
        this.timeLimit = timeLimit;
    }

    public MemoryLimit getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(MemoryLimit memoryLimit) {
        this.memoryLimit = memoryLimit;
    }
}
