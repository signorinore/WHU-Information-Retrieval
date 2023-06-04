package bean;

public class DocumentContent {
    private String docNO;
    private String docType;
    private String txtType;
    private String text;

    public String getDocNO() {
        return docNO;
    }

    public String getDocType() {
        return docType;
    }

    public String getTxtType() {
        return txtType;
    }

    public String getText() {
        return text;
    }

    public void setDocNO(String docNO) {
        this.docNO = docNO;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public void setTxtType(String txtType) {
        this.txtType = txtType;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DocumentContent(String fileContent) {
        int docNOBegin = fileContent.indexOf("<DOCNO>");
        int docNOEnd = fileContent.indexOf("</DOCNO>", docNOBegin);
        int docTypeBegin = fileContent.indexOf("<DOCTYPE>", docNOEnd);
        int docTypeEnd = fileContent.indexOf("</DOCTYPE>", docTypeBegin);
        int txtTypeBegin = fileContent.indexOf("<TXTTYPE>", docTypeEnd);
        int txtTypeEnd = fileContent.indexOf("</TXTTYPE>", txtTypeBegin);
        int textBegin = fileContent.indexOf("<TEXT>", txtTypeEnd);
        int textEnd = fileContent.indexOf("</TEXT>", textBegin);

        if (docNOBegin == -1 || docNOEnd == -1 || docTypeBegin == -1 || docTypeEnd == -1 ||
                txtTypeBegin == -1 || txtTypeEnd == -1 || textBegin == -1 || textEnd == -1) {
            int t = 5 / 0;
        }
        this.docNO = fileContent.substring(docNOBegin + 7, docNOEnd).trim();
        this.docType = fileContent.substring(docTypeBegin + 9, docTypeEnd).trim();
        this.txtType = fileContent.substring(txtTypeBegin + 9, txtTypeEnd).trim();
        this.text = fileContent.substring(textBegin + 6, textEnd).trim();
    }

    @Override
    public String toString() {
        return "DOCNO:\n\t" + getDocNO() + "\nDOCTYPE:\n\t" + getDocType() + "\nTXTTYPE:\n\t" + getTxtType() + "\nTEXT:\n\t" + getText() + '\n';
    }
}
