package com.example.Automated.Application.Mangament.enums;

public enum RoleEnum {
    ACADEMIC_STAFF_AFFAIR(0),
    TRAINEE(1),
    ADMIN(2),
    HEAD_OF_DEPARTMENT(3),
    TRAINING_DIRECTOR(4);

    private final int value;

    RoleEnum(int value){ this.value = value; }

    public int getValue(){return this.value;}
}
