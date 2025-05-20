package com.kh.enums;

public enum AppointmentSlot {
    SLOT_1(1, "07:30", "08:00"),
    SLOT_2(2, "08:00", "08:30"),
    SLOT_3(3, "08:30", "09:00"),
    SLOT_4(4, "09:00", "09:30"),
    SLOT_5(5, "09:30", "10:00"),
    SLOT_6(6, "10:00", "10:30"),
    SLOT_7(7, "10:30", "11:00"),
    SLOT_8(8, "13:00", "13:30"),
    SLOT_9(9, "13:30", "14:00"),
    SLOT_10(10, "14:00", "14:30"),
    SLOT_11(11, "14:30", "15:00"),
    SLOT_12(12, "15:00", "15:30"),
    SLOT_13(13, "15:30", "16:00"),
    SLOT_14(14, "16:00", "16:30"),
    SLOT_15(15, "16:30", "17:00"),
    SLOT_16(16, "17:00", "17:30");

    private final int slotNumber;
    private final String startTime;
    private final String endTime;

    AppointmentSlot(int slotNumber, String startTime, String endTime) {
        this.slotNumber = slotNumber;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public static AppointmentSlot fromSlotNumber(int slotNumber) {
        for (AppointmentSlot slot : values()) {
            if (slot.slotNumber == slotNumber) return slot;
        }
        throw new IllegalArgumentException("Ca khám không hợp lệ: " + slotNumber);
    }
}
