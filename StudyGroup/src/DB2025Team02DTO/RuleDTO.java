package DB2025Team02DTO;

import java.sql.Date;
import java.sql.Time;
/**
 * 스터디별 인증 규칙 및 벌금 정책을 담는 DTO 클래스입니다.
 */
public class RuleDTO {

    private int certCycle;          // 인증 주기
    private int gracePeriod;        // 유예 기간
    private int fineLate;           // 지각 벌금
    private int fineAbsent;         // 미인증 벌금
    private int ptSettleCycle;      // 보증금 정산 주기
    private Date nextCertDate;

    public RuleDTO( int certCycle, int gracePeriod,
                   int fineLate, int fineAbsent, int ptSettleCycle, Date lastModified, Date nextCertDate) {

        this.certCycle = certCycle;
        this.gracePeriod = gracePeriod;
        this.fineLate = fineLate;
        this.fineAbsent = fineAbsent;
        this.ptSettleCycle = ptSettleCycle;
        this.nextCertDate = nextCertDate;
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
    
    public Date getNextCertDate() {
    	return nextCertDate;
    }
}
