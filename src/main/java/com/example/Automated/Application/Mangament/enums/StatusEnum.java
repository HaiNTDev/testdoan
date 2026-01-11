package com.example.Automated.Application.Mangament.enums;

public enum StatusEnum {
    Pending(1),
    Approve(3),
    Reject(4),
    Complete(5),
    InProgress(2);

    private final int value;

    StatusEnum(int value){ this.value = value; }

    public int getValue(){return this.value;}
}
