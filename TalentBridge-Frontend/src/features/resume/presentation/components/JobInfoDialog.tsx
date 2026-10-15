import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/shared/presentation/ui/dialog";
import { Button } from "@/shared/presentation/ui/button";
import { BookText } from "lucide-react";
import type { Resume } from "../../domain/resume";
import RichTextPreview from "@/shared/presentation/components/RichText/index-preview";
import { ScrollArea } from "@/shared/presentation/ui/scroll-area";
import { Badge } from "@/shared/presentation/ui/badge";

type Props = {
  job: Resume["job"];
  companyName: string;
  location: string;
};

export default function JobInfoDialog({ job, companyName, location }: Props) {
  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="border border-orange-500 bg-white py-3 font-semibold text-orange-500 hover:border-orange-600 hover:bg-orange-50">
          <BookText className="mr-2 h-4 w-4" />
          Xem mô tả công việc
        </Button>
      </DialogTrigger>
      <DialogContent className="flex !h-11/12 !max-h-none !w-full !max-w-none flex-col lg:!w-2/3">
        <DialogHeader>
          <DialogTitle>{job.name}</DialogTitle>
          <DialogDescription>
            {companyName} - {location}
          </DialogDescription>
        </DialogHeader>
        <div className="flex flex-1 flex-col space-y-5">
          <div>
            <span className="font-semibold text-gray-700">
              Kỹ năng yêu cầu:
            </span>
            <div className="mt-2 flex flex-wrap gap-2">
              {job.skills.map((skill) => (
                <Badge key={skill} className="bg-orange-100 text-orange-700">
                  {skill}
                </Badge>
              ))}
            </div>
          </div>
          <div className="flex-1">
            <span className="font-semibold text-gray-700">
              Mô tả công việc:
            </span>
            <ScrollArea className="mt-2 h-[200px] rounded-lg border bg-gray-50 p-4 sm:h-[300px] md:h-[350px] lg:h-[450px]">
              <RichTextPreview content={job.description} />
            </ScrollArea>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
