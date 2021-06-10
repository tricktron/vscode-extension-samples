package ch.fhnw.thga;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.JsonPrimitive;

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
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

public class SimpleTextDocumentService implements TextDocumentService {

	private final SimpleLanguageServer simpleLanguageServer;

	public SimpleTextDocumentService(SimpleLanguageServer server) {
		this.simpleLanguageServer = server;
	}

	protected static CompletionItem createTextCompletionItem(String label, Object data) {
		CompletionItem item = new CompletionItem(label);
		item.setKind(CompletionItemKind.Text);
		item.setData(data);
		return item;
	}

	protected static Diagnostic mapMatchResultToDiagnostic(MatchResult res) {
		return new Diagnostic(new Range(new Position(0, res.start()), new Position(0, res.end())), res.group() + " is all uppercase.", DiagnosticSeverity.Warning, "ex");
	}

	protected static List<Diagnostic> findUpperCaseWordsWithLengthTwoOrMore(String text) {
		Pattern pattern = Pattern.compile("\\b[A-Z]{2,}\\b");
		Matcher matcher = pattern.matcher(text);
		return matcher.results().map(res -> mapMatchResultToDiagnostic(res)).collect(Collectors.toList());
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
	public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem item) {
		JsonPrimitive data = (JsonPrimitive) item.getData();
		if (data.getAsInt() == 1) {
			item.setDetail("TypeScript details");
			item.setDocumentation("TypeScript documentation");
			return CompletableFuture.completedFuture(item);
		} else if (data.getAsInt() == 2) {
			item.setDetail("JavaScript details");
			item.setDocumentation("JavaScript documentation");
			return CompletableFuture.completedFuture(item);
		} else {
			return CompletableFuture.completedFuture(item);
		}
	}

	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
		CompletableFuture.runAsync(() -> simpleLanguageServer.client
				.publishDiagnostics(new PublishDiagnosticsParams(params.getTextDocument().getUri(),
				findUpperCaseWordsWithLengthTwoOrMore(params.getTextDocument().getText()))));
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
