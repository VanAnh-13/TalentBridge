import { useState } from "react";
import type Quill from "quill";
import { Button } from "@/shared/presentation/ui/button";
import { Input } from "@/shared/presentation/ui/input";
import { useTranslation } from "react-i18next";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/shared/presentation/ui/dialog";

interface Props {
  editor: Quill | null;
  onCancel: () => void;
  open: boolean;
}

export default function ImageInsertModal({ editor, onCancel, open }: Props) {
  const { t } = useTranslation();
  const [inputValue, setInputValue] = useState("");

  const handleInsert = () => {
    if (editor && inputValue) {
      let range = editor.getSelection();

      if (!range) {
        editor.focus();
        const length = editor.getLength();
        editor.setSelection(length, 0);
        range = editor.getSelection();
      }

      const index = range?.index ?? 0;

      editor.insertEmbed(index, "image", inputValue, "user");
      editor.setSelection(index + 1);
      editor.focus();
    }

    setInputValue("");
    onCancel();
  };

  return (
    <Dialog open={open} onOpenChange={onCancel}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>{t("editor.imageUrlTitle")}</DialogTitle>
        </DialogHeader>
        <div className="space-y-4">
          <Input
            type="text"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            placeholder="https://example.com/image.jpg"
          />
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={onCancel}>
              {t("actions.cancel")}
            </Button>
            <Button onClick={handleInsert}>{t("editor.insertImage")}</Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
