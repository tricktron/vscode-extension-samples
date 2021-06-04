package ch.fhnw.thga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

public class SimpleTextDocumentService implements TextDocumentService {

	private final SimpleLanguageServer simpleLanguageServer;
	private final Map<String, String> docs = Collections.synchronizedMap(new HashMap<>());

	public SimpleTextDocumentService(SimpleLanguageServer server) {
		this.simpleLanguageServer = server;
	}

	protected static CompletionItem createTextCompletionItem(String label, Object data) {
		CompletionItem item = new CompletionItem(label);
		item.setKind(CompletionItemKind.Text);
		item.setData(data);
		return item;
	}

	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
		List<CompletionItem> completionItems = Arrays.asList(
			createTextCompletionItem("TypeScript", 1),
			createTextCompletionItem("JavaScript", 2)
		);
		return CompletableFuture.completedFuture(Either.forLeft(completionItems));
	}

	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
		String text = params.getTextDocument().getText();
		this.docs.put(params.getTextDocument().getUri(), text);
		List<Diagnostic> res = new ArrayList<>();
		Diagnostic diagnostic = new Diagnostic();
		diagnostic.setSeverity(DiagnosticSeverity.Information);
		diagnostic.setMessage("Opened a text document");
		res.add(diagnostic);
		CompletableFuture.runAsync(() -> simpleLanguageServer.client
				.publishDiagnostics(new PublishDiagnosticsParams(params.getTextDocument().getUri(), res)));

	}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
	}
}
