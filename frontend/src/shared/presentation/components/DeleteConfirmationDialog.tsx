import {
  AlertDialog,
  AlertDialogTrigger,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogCancel,
  AlertDialogAction,
} from "@/shared/presentation/ui/alert-dialog";
import { Button } from "@/shared/presentation/ui/button";
import { Trash2 } from "lucide-react";
import { type ReactNode } from "react";
import { useTranslation } from "react-i18next";

interface DeleteConfirmDialogProps {
  onConfirm: () => void;
  description?: string;
  title?: string;
  children?: ReactNode;
  styledDescription?: ReactNode;
}

export const DeleteConfirmDialog = ({
  onConfirm,
  description,
  styledDescription,
  title,
  children,
}: DeleteConfirmDialogProps) => {
  const { t } = useTranslation();
  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        {children ?? (
          <Button
            variant="ghost"
            size="icon"
            className="h-8 w-8 text-red-500 hover:text-red-600"
            onClick={(e) => e.stopPropagation()}
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        )}
      </AlertDialogTrigger>
      <AlertDialogContent
        onClick={(e) => e.stopPropagation()}
        className="z-[9999]"
      >
        <AlertDialogHeader>
          <AlertDialogTitle>{title ?? t("deleteDialog.title")}</AlertDialogTitle>
          <AlertDialogDescription>
            {styledDescription ? styledDescription : (description ?? t("deleteDialog.description"))}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>{t("actions.cancel")}</AlertDialogCancel>
          <AlertDialogAction onClick={onConfirm} className="bg-orange-500">
            {t("deleteDialog.confirm")}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
