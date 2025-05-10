package model;

import java.util.Date;

public class Deposit {
    private int depositId;
    private int userId;
    private int studyId;
    private int amount;
    private Date depositDate;
    private boolean isRefunded;

    public Deposit(int depositId, int userId, int studyId, int amount, Date depositDate, boolean isRefunded) {
        this.depositId = depositId;
        this.userId = userId;
        this.studyId = studyId;
        this.amount = amount;
        this.depositDate = depositDate;
        this.isRefunded = isRefunded;
    }

    // Getter & Setter
}
