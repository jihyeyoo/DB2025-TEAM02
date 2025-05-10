package model;

import java.util.Date;

public class Fine {
    private int fineId;
    private int userId;
    private int studyId;
    private boolean isPaid;
    private String reason;
    private int amount;
    private Date date;

    public Fine(int fineId, int userId, int studyId, boolean isPaid, String reason, int amount, Date date) {
        this.fineId = fineId;
        this.userId = userId;
        this.studyId = studyId;
        this.isPaid = isPaid;
        this.reason = reason;
        this.amount = amount;
        this.date = date;
    }

    // Getter & Setter
}

