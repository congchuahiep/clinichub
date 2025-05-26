export const SLOT_LABELS = [
  "07:30 - 08:00", "08:00 - 08:30", "08:30 - 09:00", "09:00 - 09:30",
  "09:30 - 10:00", "10:00 - 10:30", "10:30 - 11:00", "13:00 - 13:30",
  "13:30 - 14:00", "14:00 - 14:30", "14:30 - 15:00", "15:00 - 15:30",
  "15:30 - 16:00", "16:00 - 16:30", "16:30 - 17:00", "17:00 - 17:30"
];

export const STATUS_MAP = {
  scheduled: { label: "Đã đặt", variant: "secondary" },
  rescheduled: { label: "Đã đặt", variant: "warning" },
  completed: { label: "Hoàn thành", variant: "success" },
  cancelled: { label: "Đã huỷ", variant: "danger" }
};