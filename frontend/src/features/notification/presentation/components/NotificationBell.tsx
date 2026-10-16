import { Bell, CheckCheck } from "lucide-react";
import { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";

import { notifications as notificationUseCases } from "@/app/container";
import { errorMessage } from "@/shared/domain/errors";
import { formatLoginTime } from "@/shared/presentation/format";
import { Badge } from "@/shared/presentation/ui/badge";
import { Button } from "@/shared/presentation/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/shared/presentation/ui/dropdown-menu";

import type { Notification } from "../../domain/notification";

/** The bell with the unread count; new notifications appear live while the page is open. */
const NotificationBell = () => {
  const { t } = useTranslation("notification");
  const navigate = useNavigate();
  const [items, setItems] = useState<Notification[]>([]);
  const [unread, setUnread] = useState(0);

  const refresh = useCallback(async () => {
    try {
      const [page, count] = await Promise.all([
        notificationUseCases.recent(),
        notificationUseCases.unreadCount(),
      ]);
      setItems(page.content);
      setUnread(count);
    } catch {
      /* the bell is a convenience: stay quiet when it cannot load */
    }
  }, []);

  useEffect(() => {
    void refresh();
    return notificationUseCases.onArrival((notification) => {
      toast.info(notification.title, { description: notification.body ?? undefined });
      void refresh();
    });
  }, [refresh]);

  const open = async (notification: Notification) => {
    try {
      if (!notification.read) await notificationUseCases.markRead(notification.id);
    } catch (err) {
      toast.error(errorMessage(err, t("bell.updateFailed")));
    }
    void refresh();
    if (notification.link) navigate(notification.link);
  };

  const readAll = async () => {
    try {
      await notificationUseCases.markAllRead();
      void refresh();
    } catch (err) {
      toast.error(errorMessage(err, t("bell.updateFailed")));
    }
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon" className="relative" aria-label={t("bell.title")}>
          <Bell className="h-5 w-5" />
          {unread > 0 && (
            <Badge className="absolute -right-1 -top-1 h-5 min-w-5 justify-center rounded-full bg-red-500 px-1 text-xs text-white">
              {unread > 99 ? "99+" : unread}
            </Badge>
          )}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-80">
        <div className="flex items-center justify-between pr-2">
          <DropdownMenuLabel>{t("bell.title")}</DropdownMenuLabel>
          {unread > 0 && (
            <Button variant="ghost" size="sm" onClick={readAll} className="h-7 gap-1 text-xs">
              <CheckCheck className="h-3 w-3" /> {t("bell.markAllRead")}
            </Button>
          )}
        </div>
        <DropdownMenuSeparator />
        {items.length === 0 ? (
          <p className="p-4 text-center text-sm text-gray-500">{t("bell.empty")}</p>
        ) : (
          <ul className="max-h-96 overflow-y-auto">
            {items.map((n) => (
              <li key={n.id}>
                <button
                  type="button"
                  onClick={() => open(n)}
                  className={`w-full border-b px-3 py-2 text-left hover:bg-gray-50 ${n.read ? "" : "bg-blue-50"}`}
                >
                  <p className="text-sm font-medium">{n.title}</p>
                  {n.body && <p className="line-clamp-2 text-xs text-gray-600">{n.body}</p>}
                  <p className="mt-1 text-xs text-gray-400">{formatLoginTime(n.createdAt)}</p>
                </button>
              </li>
            ))}
          </ul>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default NotificationBell;
