package com.example.Automated.Application.Mangament.enums;

public enum BatchEnum {
    Draft(1),
    Published(2),
    Open(3),
    Close(4);
    private final int value;

    BatchEnum(int value){ this.value = value; }

    public int getValue(){return this.value;}
}
