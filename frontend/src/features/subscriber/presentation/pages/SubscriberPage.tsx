import { errorMessage } from "@/shared/domain/errors";
import { subscribers } from "@/app/container";
import type { SkillRef } from "@/features/skill/domain/skill";
import type { Subscriber } from "../../domain/subscriber";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "sonner";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/presentation/ui/card";
import { Button } from "@/shared/presentation/ui/button";
import { Badge } from "@/shared/presentation/ui/badge";
import { Separator } from "@/shared/presentation/ui/separator";

import {
  Bell,
  BellOff,
  CheckCircle,
  Edit,
  Loader2,
  Save,
  Trash,
  User,
  Wrench,
} from "lucide-react";
import { Alert, AlertDescription } from "@/shared/presentation/ui/alert";
import { SkillSelection } from "@/features/skill";
import { DeleteConfirmDialog } from "@/shared/presentation/components/DeleteConfirmationDialog";

const UserSubscriberPage = () => {
  const { t } = useTranslation(["subscriber", "common"]);

  // Data
  const [subscriber, setSubscriber] =
    useState<Subscriber | null>();

  const [isLoading, setIsLoading] = useState(false);
  const [isChecking, setIsChecking] = useState(false);
  const [isEditing, setIsEditing] = useState(false);

  // Form State
  const [selectedSkills, setSelectedSkills] = useState<SkillRef[]>([]);

  // =========================================================
  // Handle Fetching
  // =========================================================
  const fetchSubscriber = async () => {
    try {
      setIsChecking(true);
      const subscriberData = await subscribers.mine();
      setSubscriber(subscriberData);

      // Set selected skills from subscriber data
      if (subscriberData?.skills) {
        setSelectedSkills(
          subscriberData.skills.map((skill: SkillRef) => ({
            id: skill.id,
            name: skill.name,
          })),
        );
      }
    } catch {
      setSubscriber(null);
      setSelectedSkills([]);
    } finally {
      setIsChecking(false);
    }
  };

  useEffect(() => {
    fetchSubscriber();
  }, []);

  // =========================================================
  // Handle Create or Update
  // =========================================================
  const handleCreateOrUpdateSubscriber = async () => {
    if (selectedSkills.length === 0) {
      toast.error(t("toast.skillRequired"));
      return;
    }

    try {
      setIsLoading(true);

      const isUpdate = !!subscriber;
      const saved = await subscribers.save(
        selectedSkills.map((s) => s.id),
        isUpdate,
      );
      toast.success(
        isUpdate ? t("toast.updated") : t("toast.subscribed"),
      );

      setSubscriber(saved);
      setIsEditing(false);
    } catch (err) {
      toast.error(errorMessage(err, t("toast.failed")));
    } finally {
      setIsLoading(false);
    }
  };

  // =========================================================
  // Handle Delete
  // =========================================================
  const handleDelete = async () => {
    try {
      setIsLoading(true);

      await subscribers.unsubscribe();
      setSubscriber(null);
      setSelectedSkills([]);
      setIsEditing(false);
      toast.success(t("toast.unsubscribed"));
    } catch (err) {
      toast.error(errorMessage(err, t("toast.failed")));
    } finally {
      setIsLoading(false);
    }
  };

  // =========================================================
  // Skill selection handler
  // =========================================================
  const addSkill = (skill: SkillRef) => {
    const exists = selectedSkills.some((s) => s.id === skill.id);
    if (exists) return;

    const updated = [...selectedSkills, skill];
    setSelectedSkills(updated);
  };

  const removeSkill = (skill: SkillRef) => {
    const updated = selectedSkills.filter((s) => s.id !== skill.id);
    setSelectedSkills(updated);
  };

  // =========================================================
  // Handle Edit Mode
  // =========================================================
  const handleStartEdit = () => {
    setIsEditing(true);
    // Reset selected skills to current subscriber skills
    if (subscriber?.skills) {
      setSelectedSkills(
        subscriber.skills.map((skill: SkillRef) => ({
          id: skill.id,
          name: skill.name,
        })),
      );
    }
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
    // Reset selected skills to current subscriber skills
    if (subscriber?.skills) {
      setSelectedSkills(
        subscriber.skills.map((skill: SkillRef) => ({
          id: skill.id,
          name: skill.name,
        })),
      );
    } else {
      setSelectedSkills([]);
    }
  };

  if (isChecking) {
    return (
      <div className="container mx-auto max-w-4xl p-6">
        <div className="flex min-h-[400px] items-center justify-center">
          <div className="flex flex-col items-center gap-4">
            <Loader2 className="text-primary h-8 w-8 animate-spin" />
            <p className="text-muted-foreground">{t("common:states.loading")}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto max-w-4xl space-y-6 p-6">
      {/* Header */}
      <div className="flex items-center gap-3">
        <div>
          <h1 className="text-3xl font-bold">
            {t("page.title")}
          </h1>
          <p className="text-muted-foreground mt-1">
            {t("page.subtitle")}
          </p>
        </div>
      </div>

      <Separator />

      {/* Current Subscription Status */}
      {subscriber && !isEditing && (
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <CheckCircle className="h-5 w-5 text-green-500" />
                <CardTitle className="text-xl">{t("current.title")}</CardTitle>
              </div>
            </div>
            <CardDescription>
              {t("current.description")}
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <h4 className="mb-2 flex items-center gap-2 font-medium">
                <Wrench className="h-4 w-4" />
                {t("current.skills", { count: subscriber.skills?.length || 0 })}
              </h4>
              <div className="flex flex-wrap gap-2">
                {subscriber.skills?.map((skill: SkillRef) => (
                  <Badge
                    key={skill.id}
                    variant="outline"
                    className="bg-orange-100 text-orange-700"
                  >
                    {skill.name}
                  </Badge>
                ))}
              </div>
            </div>

            <div className="flex gap-2 pt-4">
              <Button
                onClick={handleStartEdit}
                variant="outline"
                className="border border-orange-500 bg-white py-3 font-semibold text-orange-500 hover:border-orange-600 hover:bg-orange-50"
              >
                <Edit className="mr-2 h-4 w-4" />
                {t("common:actions.edit")}
              </Button>

              <DeleteConfirmDialog
                onConfirm={handleDelete}
                description={t("current.unsubscribeConfirm")}
              >
                <Button variant="destructive">
                  <Trash className="mr-2 h-4 w-4" />
                  {t("current.unsubscribe")}
                </Button>
              </DeleteConfirmDialog>
            </div>
          </CardContent>
        </Card>
      )}

      {/* No Subscription State */}
      {!subscriber && !isEditing && (
        <Card>
          <CardHeader className="text-center">
            <div className="mb-4 flex justify-center">
              <BellOff className="text-muted-foreground h-16 w-16" />
            </div>
            <CardTitle className="text-xl">
              {t("none.title")}
            </CardTitle>
            <CardDescription>
              {t("none.description")}
            </CardDescription>
          </CardHeader>
          <CardContent className="text-center">
            <Button onClick={() => setIsEditing(true)} size="lg">
              <Bell className="mr-2 h-4 w-4" />
              {t("none.subscribe")}
            </Button>
          </CardContent>
        </Card>
      )}

      {/* Edit/Create Form */}
      {isEditing && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <User className="h-5 w-5" />
              {subscriber ? t("form.editTitle") : t("form.createTitle")}
            </CardTitle>
            <CardDescription>
              {subscriber
                ? t("form.editDescription")
                : t("form.createDescription")}
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <Alert>
              <Bell className="h-4 w-4" />
              <AlertDescription>
                {t("form.hint")}
              </AlertDescription>
            </Alert>

            <SkillSelection
              selectedSkills={selectedSkills}
              onAddSkill={addSkill}
              onRemoveSkill={removeSkill}
            />

            <div className="flex gap-2 pt-4">
              <Button
                onClick={handleCreateOrUpdateSubscriber}
                disabled={isLoading || selectedSkills.length === 0}
              >
                {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                <Save className="mr-2 h-4 w-4" />
                {subscriber ? t("common:actions.update") : t("form.subscribe")}
              </Button>

              <Button
                variant="outline"
                onClick={handleCancelEdit}
                disabled={isLoading}
              >
                {t("common:actions.cancel")}
              </Button>
            </div>

            {selectedSkills.length === 0 && (
              <Alert>
                <AlertDescription className="text-amber-600">
                  {t("form.skillRequired")}
                </AlertDescription>
              </Alert>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  );
};

export default UserSubscriberPage;
