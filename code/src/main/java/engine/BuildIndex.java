package engine;

import bean.DocumentContent;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class BuildIndex {
    public static void simpleIndex() throws IOException {
        Directory directory = FSDirectory.open(Paths.get(FileUtils.getProjectPath() + "\\index"));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        File tdt3 = new File(FileUtils.getProjectPath() + "\\tdt3");

        File[] childDirs = tdt3.listFiles();

        for (int i = 0; i < childDirs.length; i++) {
            File[] txtList = childDirs[i].listFiles();
            for (File file : txtList) {
                String fileName = file.getName();
                String filePath = file.getPath();
                long fileSize = FileUtils.getFileSize(file);
                DocumentContent documentContent = new DocumentContent(FileUtils.getFileContent(file));

                Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);
                Field filePathField = new StoredField("filePath", filePath);
                Field fileSizeField = new StoredField("fileSize", fileSize);

                Field docNOField = new StoredField("docNO", documentContent.getDocNO());
                Field docTypeField = new StringField("docType", documentContent.getDocType(), Field.Store.YES);
                Field txtTypeField = new StringField("txtType", documentContent.getTxtType(), Field.Store.YES);

                FieldType ft = new FieldType();
                ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);// 存储
                ft.setStored(false);
                ft.setStoreTermVectors(true);
                ft.setTokenized(true);
                ft.setStoreTermVectorPositions(true);
                ft.setStoreTermVectorOffsets(true);
                Field textField = new Field("text", documentContent.getText(), ft);

                Document document = new Document();
                document.add(fileNameField);
                document.add(filePathField);
                document.add(fileSizeField);
                document.add(docNOField);
                document.add(docTypeField);
                document.add(txtTypeField);
                document.add(textField);

                indexWriter.addDocument(document);
            }
        }
        indexWriter.close();
    }

    public static void main(String []args)throws Exception {
        simpleIndex();
    }
}
