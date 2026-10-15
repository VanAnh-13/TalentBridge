export interface Notification {
  id: number;
  type: string;
  title: string;
  body: string | null;
  /** App route to open when the notification is clicked. */
  link: string | null;
  read: boolean;
  createdAt: string;
}
