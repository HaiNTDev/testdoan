package com.example.Automated.Application.Mangament.enums;

public enum MatrixStatusEnum {
    Drafted(0);

    private final int value;

    MatrixStatusEnum(int value){ this.value = value; }

    public int getValue(){return this.value;}
}
