package model;

import java.util.Date;

public class Rule {
    private int ruleId;
    private int studyId;
    private String certDeadline;
    private int certCycle;
    private int gracePeriod;
    private int fineLate;
    private int fineAbsent;
    private int ptSettleCycle;
    private Date lastModified;

    public Rule(int ruleId, int studyId, String certDeadline, int certCycle, int gracePeriod,
                 int fineLate, int fineAbsent, int ptSettleCycle, Date lastModified) {
        this.ruleId = ruleId;
        this.studyId = studyId;
        this.certDeadline = certDeadline;
        this.certCycle = certCycle;
        this.gracePeriod = gracePeriod;
        this.fineLate = fineLate;
        this.fineAbsent = fineAbsent;
        this.ptSettleCycle = ptSettleCycle;
        this.lastModified = lastModified;
    }

    // Getter & Setter
}
