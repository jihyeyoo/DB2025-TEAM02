package DB2025Team02DTO;

import java.sql.Date;
import java.sql.Time;

public class RuleDTO {
    private Time certDeadline;      // 인증 마감 시간
    private int certCycle;          // 인증 주기
    private int gracePeriod;        // 유예 기간
    private int fineLate;           // 지각 벌금
    private int fineAbsent;         // 미인증 벌금
    private int ptSettleCycle;      // 보증금 정산 주기
    private Date lastModified;      // 규칙 마지막 수정일

    public RuleDTO(Time certDeadline, int certCycle, int gracePeriod,
                   int fineLate, int fineAbsent, int ptSettleCycle, Date lastModified) {
        this.certDeadline = certDeadline;
        this.certCycle = certCycle;
        this.gracePeriod = gracePeriod;
        this.fineLate = fineLate;
        this.fineAbsent = fineAbsent;
        this.ptSettleCycle = ptSettleCycle;
        this.lastModified = lastModified;
    }

    public Time getCertDeadline() {
        return certDeadline;
    }

    public int getCertCycle() {
        return certCycle;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public int getFineLate() {
        return fineLate;
    }

    public int getFineAbsent() {
        return fineAbsent;
    }

    public int getPtSettleCycle() {
        return ptSettleCycle;
    }
}
