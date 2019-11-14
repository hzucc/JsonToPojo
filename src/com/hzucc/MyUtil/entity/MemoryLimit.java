/*
 *@author ChenCheng
 *@date 2019/11/14
 */
package com.hzucc.MyUtil.entity;

public class MemoryLimit {
    private int memoryLimitId;
    private int problemId;
    private short cMemoryLimit;
    private short c_cppMemoryLimit;
    private short javaMemoryLimit;
    private short goMemoryLimit;
    private short python3MemoryLimit;

    @Override
    public String toString() {
        return "MemoryLimit{" +
                "memoryLimitId=" + memoryLimitId +
                ", problemId=" + problemId +
                ", cMemoryLimit=" + cMemoryLimit +
                ", c_cppMemoryLimit=" + c_cppMemoryLimit +
                ", javaMemoryLimit=" + javaMemoryLimit +
                ", goMemoryLimit=" + goMemoryLimit +
                ", python3MemoryLimit=" + python3MemoryLimit +
                '}';
    }

    public int getMemoryLimitId() {
        return memoryLimitId;
    }

    public void setMemoryLimitId(int memoryLimitId) {
        this.memoryLimitId = memoryLimitId;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public short getcMemoryLimit() {
        return cMemoryLimit;
    }

    public void setcMemoryLimit(short cMemoryLimit) {
        this.cMemoryLimit = cMemoryLimit;
    }

    public short getC_cppMemoryLimit() {
        return c_cppMemoryLimit;
    }

    public void setC_cppMemoryLimit(short c_cppMemoryLimit) {
        this.c_cppMemoryLimit = c_cppMemoryLimit;
    }

    public short getJavaMemoryLimit() {
        return javaMemoryLimit;
    }

    public void setJavaMemoryLimit(short javaMemoryLimit) {
        this.javaMemoryLimit = javaMemoryLimit;
    }

    public short getGoMemoryLimit() {
        return goMemoryLimit;
    }

    public void setGoMemoryLimit(short goMemoryLimit) {
        this.goMemoryLimit = goMemoryLimit;
    }

    public short getPython3MemoryLimit() {
        return python3MemoryLimit;
    }

    public void setPython3MemoryLimit(short python3MemoryLimit) {
        this.python3MemoryLimit = python3MemoryLimit;
    }
}
