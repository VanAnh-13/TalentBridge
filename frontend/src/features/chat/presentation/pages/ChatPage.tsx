import { Check, CheckCheck, Send } from "lucide-react";
import { useCallback, useEffect, useRef, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { toast } from "sonner";

import { chat as chatUseCases } from "@/app/container";
import { useAccount } from "@/features/auth";
import { errorMessage } from "@/shared/domain/errors";
import { EmptyState } from "@/shared/presentation/components/EmptyState";
import { Badge } from "@/shared/presentation/ui/badge";
import { Button } from "@/shared/presentation/ui/button";
import { Input } from "@/shared/presentation/ui/input";

import { MAX_LENGTH, merge, sendable } from "../../domain/chat";
import type { ChatMessage, Conversation } from "../../domain/chat";

const time = (iso: string) =>
  new Date(iso).toLocaleString("vi-VN", { hour: "2-digit", minute: "2-digit", day: "2-digit", month: "2-digit" });

/** Conversations on the left, the open thread on the right; new messages appear live. */
const ChatPage = () => {
  const me = Number(useAccount().id);
  const [params, setParams] = useSearchParams();

  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [selected, setSelected] = useState<number | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [hasOlder, setHasOlder] = useState(false);
  const [olderPage, setOlderPage] = useState(1);
  const [text, setText] = useState("");
  const [sending, setSending] = useState(false);
  /** For each conversation: the newest of my messages the other side has read. */
  const [readUpTo, setReadUpTo] = useState<Record<number, number>>({});
  const endRef = useRef<HTMLDivElement | null>(null);
  const selectedRef = useRef<number | null>(null);
  selectedRef.current = selected;

  const loadConversations = useCallback(async () => {
    try {
      setConversations((await chatUseCases.conversations()).content);
    } catch (err) {
      toast.error(errorMessage(err, "Không thể tải danh sách trò chuyện"));
    }
  }, []);

  // open what the URL asks for: ?resume=<application id> (creates the thread) or ?conversation=<id>
  useEffect(() => {
    const resume = params.get("resume");
    const conversation = params.get("conversation");
    void (async () => {
      await loadConversations();
      try {
        if (resume) {
          const opened = await chatUseCases.open(Number(resume));
          setSelected(opened.id);
          await loadConversations();
        } else if (conversation) {
          setSelected(Number(conversation));
        }
      } catch (err) {
        toast.error(errorMessage(err, "Không thể mở cuộc trò chuyện"));
      }
      if (resume || conversation) setParams({}, { replace: true });
    })();
    // only on first load
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // load the thread when another conversation is selected
  useEffect(() => {
    if (selected === null) return;
    setMessages([]);
    setOlderPage(1);
    void (async () => {
      try {
        const history = await chatUseCases.history(selected, 1);
        setMessages(history.messages);
        setHasOlder(history.hasOlder);
        await chatUseCases.markRead(selected);
        setConversations((list) => list.map((c) => (c.id === selected ? { ...c, unread: 0 } : c)));
      } catch (err) {
        toast.error(errorMessage(err, "Không thể tải tin nhắn"));
      }
    })();
  }, [selected]);

  // live events
  useEffect(
    () =>
      chatUseCases.listen({
        onMessage: (message) => {
          const open = selectedRef.current === message.conversationId;
          if (open) {
            setMessages((list) => merge(list, [message]));
            void chatUseCases.markRead(message.conversationId);
          }
          setConversations((list) => {
            const known = list.some((c) => c.id === message.conversationId);
            if (!known) {
              void loadConversations();
              return list;
            }
            return list
              .map((c) =>
                c.id === message.conversationId
                  ? { ...c, lastMessage: message, lastMessageAt: message.sentAt, unread: open ? 0 : c.unread + 1 }
                  : c,
              )
              .sort((a, b) => b.lastMessageAt.localeCompare(a.lastMessageAt));
          });
        },
        onRead: (receipt) =>
          setReadUpTo((map) => ({ ...map, [receipt.conversationId]: Math.max(map[receipt.conversationId] ?? 0, receipt.upToMessageId) })),
      }),
    [loadConversations],
  );

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages.length, selected]);

  const loadOlder = async () => {
    if (selected === null) return;
    try {
      const next = olderPage + 1;
      const history = await chatUseCases.history(selected, next);
      setMessages((list) => merge(list, history.messages));
      setHasOlder(history.hasOlder);
      setOlderPage(next);
    } catch (err) {
      toast.error(errorMessage(err, "Không thể tải tin nhắn cũ"));
    }
  };

  const send = async () => {
    if (selected === null || !sendable(text)) return;
    setSending(true);
    try {
      const sent = await chatUseCases.send(selected, text);
      setMessages((list) => merge(list, [{ ...sent, senderName: "Bạn" }]));
      setText("");
      setConversations((list) =>
        list.map((c) => (c.id === selected ? { ...c, lastMessage: sent, lastMessageAt: sent.sentAt } : c)),
      );
    } catch (err) {
      toast.error(errorMessage(err, "Không thể gửi tin nhắn"));
    } finally {
      setSending(false);
    }
  };

  const current = conversations.find((c) => c.id === selected);

  return (
    <div className="mx-auto flex h-[75vh] max-w-6xl overflow-hidden rounded-lg border bg-white shadow">
      <aside className="w-72 shrink-0 overflow-y-auto border-r">
        <h2 className="border-b p-3 font-semibold">Tin nhắn</h2>
        {conversations.length === 0 ? (
          <p className="p-4 text-sm text-gray-500">Chưa có cuộc trò chuyện nào. Mở từ hồ sơ ứng tuyển.</p>
        ) : (
          conversations.map((c) => (
            <button
              key={c.id}
              type="button"
              onClick={() => setSelected(c.id)}
              className={`w-full border-b p-3 text-left hover:bg-gray-50 ${c.id === selected ? "bg-blue-50" : ""}`}
            >
              <div className="flex items-center justify-between gap-2">
                <span className="truncate text-sm font-medium">{c.title}</span>
                {c.unread > 0 && <Badge className="bg-red-500 text-white">{c.unread}</Badge>}
              </div>
              <p className="truncate text-xs text-gray-500">{c.subtitle}</p>
              {c.lastMessage && <p className="truncate text-xs text-gray-600">{c.lastMessage.text}</p>}
            </button>
          ))
        )}
      </aside>

      <section className="flex min-w-0 flex-1 flex-col">
        {!current ? (
          <div className="flex flex-1 items-center justify-center">
            <EmptyState title="Chọn một cuộc trò chuyện" description="Tin nhắn sẽ hiện ở đây." />
          </div>
        ) : (
          <>
            <header className="border-b p-3">
              <p className="font-semibold">{current.title}</p>
              <p className="text-xs text-gray-500">{current.subtitle}</p>
            </header>
            <div className="flex-1 space-y-2 overflow-y-auto p-4">
              {hasOlder && (
                <div className="text-center">
                  <Button variant="ghost" size="sm" onClick={loadOlder}>
                    Xem tin nhắn cũ hơn
                  </Button>
                </div>
              )}
              {messages.map((m) => {
                const mine = m.senderId === me;
                const seen = mine && (readUpTo[m.conversationId] ?? 0) >= m.id;
                return (
                  <div key={m.id} className={`flex ${mine ? "justify-end" : "justify-start"}`}>
                    <div className={`max-w-[75%] rounded-lg px-3 py-2 text-sm ${mine ? "bg-blue-600 text-white" : "bg-gray-100"}`}>
                      {!mine && <p className="mb-0.5 text-xs font-medium text-gray-600">{m.senderName}</p>}
                      <p className="whitespace-pre-wrap break-words">{m.text}</p>
                      <p className={`mt-1 flex items-center justify-end gap-1 text-[10px] ${mine ? "text-blue-100" : "text-gray-400"}`}>
                        {time(m.sentAt)}
                        {mine && (seen ? <CheckCheck className="h-3 w-3" aria-label="Đã xem" /> : <Check className="h-3 w-3" aria-label="Đã gửi" />)}
                      </p>
                    </div>
                  </div>
                );
              })}
              <div ref={endRef} />
            </div>
            <form
              className="flex gap-2 border-t p-3"
              onSubmit={(e) => {
                e.preventDefault();
                void send();
              }}
            >
              <Input
                value={text}
                maxLength={MAX_LENGTH}
                placeholder="Nhập tin nhắn..."
                onChange={(e) => setText(e.target.value)}
                aria-label="Tin nhắn"
              />
              <Button type="submit" disabled={sending || !sendable(text)}>
                <Send className="mr-1 h-4 w-4" /> Gửi
              </Button>
            </form>
          </>
        )}
      </section>
    </div>
  );
};

export default ChatPage;
