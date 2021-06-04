package ch.fhnw.thga;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.lsp4j.CompletionItem;

class AppTest {
    @Test void canCreateTextCompletionItem() {
        String label = "label";
        int data = 1;
        CompletionItem item = SimpleTextDocumentService.createTextCompletionItem(label, data);
        assertEquals(label, item.getLabel());
        assertEquals(data, item.getData());
    }
}
