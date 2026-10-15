import { interviews as interviewUseCases } from "@/app/container";

/** Fetches the .ics file (it needs the access token, so a plain link would not work) and saves it. */
export const downloadCalendar = async (interviewId: number) => {
  const file = await interviewUseCases.calendar(interviewId);
  const url = URL.createObjectURL(file);
  const link = document.createElement("a");
  link.href = url;
  link.download = `interview-${interviewId}.ics`;
  link.click();
  URL.revokeObjectURL(url);
};
