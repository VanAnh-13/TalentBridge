interface PDFViewerProps {
  fileUrl: string;
  className?: string;
}

/**
 * Shows a PDF with the browser's own viewer. CVs are uploaded by strangers, so they are never parsed by
 * JavaScript running in our page (pdf.js < 4.2.67 could be made to execute script by a crafted file).
 */
const PDFViewer: React.FC<PDFViewerProps> = ({
  fileUrl,
  className = "h-full w-full",
}) => {
  if (!fileUrl) return null;

  return (
    <iframe
      src={fileUrl}
      title="Xem hồ sơ (PDF)"
      referrerPolicy="no-referrer"
      className={`border border-gray-300 ${className}`}
    />
  );
};

export default PDFViewer;
