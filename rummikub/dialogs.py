from textual.app import ComposeResult
from textual.containers import Container
from textual.widgets import MarkdownViewer, Static

class MarkdownDialog(Container):
    """A floating dialog with markdown content."""

    CSS_PATH = "resources/rummikub.tcss"

    def __init__(self, title: str, message: str, show_table_of_contents: bool = True, classes: str = "dialog"):
        super().__init__()
        self.title = title
        self.message = message
        self.show_table_of_contents = show_table_of_contents
        self.classes = classes

    def compose(self) -> ComposeResult:
        yield Static(self.title, classes="title")
        yield MarkdownViewer(self.message, show_table_of_contents=self.show_table_of_contents, classes=self.classes)


