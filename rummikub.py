import argparse
from emoji import emojize
from rummikub import (
    MarkdownDialog,
    Player,
    PlayerWidget,
    Pool,
    PoolWidget,
    RummikubController,
    Table,
    TableWidget
)

from textual import events
from textual.app import App, ComposeResult
from textual.binding import Binding
from textual.widgets import Header, Static, Footer

HELP = """\
# Rummikub Game

The Rummikub game is a classic tile-based game that combines elements of rummy and mahjong.
This implementation allows you to play Rummikub in the terminal with both human and computer players.

## How to Play

1. **Setup**: The game can be played with 2 to 4 players. You can specify the number of human players and computer players.
2. **Objective**: The goal is to be the first player to empty your hand by forming valid sets of tiles.
3. **Turns**: Players take turns drawing tiles from the pool and forming sets (runs or groups) on the table.
4. **Winning**: The game ends when a player has no tiles left.

- Sequences: same color, consecutive numbers.
- Groups: same number, different colors.
- Joker can replace any tile.

## Features

- Supports both human and computer players.
- Allows for different strategies for computer players.
- Displays the current game state, including player hands and melds on the table.
- Can be run with a specific seed for reproducibility.

## Controls

- Press **?** to show the help dialog.
- Press **Esc** to close the help dialog.
- Press `Ctrl`+`Shift`+`D`: Toggle debug mode to display all players' hands.
- Use the command line arguments to configure the game settings.

## Command Line Arguments
- `--debug`: Display all players' hands.
- `--total-players`: Total number of players (2-4).
- `--human-players`: Number of human players (0 to `total-players`).
- `--seed`: The seed for randomization.
- `--strategy`: Strategy for computer players (`highest-value`, `longest-run`, `all-in`).
- `--stop-at-turn`: If specified, print a debug message that can be used to break on the debugger. Not stopping if `-1`.
- `--sort-table`: Sort melds when displaying the table.

Enjoy your game of Rummikub!

"""

class RummikubApp(App):
    """A terminal-based Rummikub game using Textual.

    Args:
        App (textual.app.App): The base Textual App class.

    """

    CSS_PATH = "resources/rummikub.tcss"

    BINDINGS = [
        Binding(key="q", action="quit", description="Quit"),
        Binding(key="p", action="play_turn", description="Play 1 turn"),
        Binding(key="n", action="next_player", description="Next player"),
        Binding(key="h", action="toggle_help", description="Help"),
    ]

    def __init__(self, args:argparse.Namespace, **kwargs):
        super().__init__(**kwargs)

        self.game = RummikubController(
            self,
            args.total_players,
            args.human_players,
            args.seed,
            {
                'strategy': args.strategy,
                'sort-table': args.sort_table,
                'stop-at-turn': args.stop_at_turn,
                'debug': args.debug,
            }
        )
        self.debug_mode = args.debug
        self.help_dialog = None

    def compose(self) -> ComposeResult:
        self.pool_widget = PoolWidget()
        self.player_widget = PlayerWidget()
        self.table_widget = TableWidget()
        self.status_widget = Static(emojize("App Ready"), id="status")

        yield Header()
        if self.debug_mode:
            yield self.pool_widget
        yield self.player_widget
        yield self.table_widget
        yield self.status_widget
        yield Footer()

    def on_mount(self) -> None:
        self.title = emojize(":game_die: Rummikub Game :game_die:")
        self.game.initialize()

    #
    # Actions
    #
    # @on(Button.Pressed, "#quit")
    # def quit_button(self) -> None:
    #     self.quit()
    def action_quit(self) -> None:
        self.exit(emojize("\n:waving_hand: bye bye!"))

    def action_play_turn(self) -> None:
        self.game.play_turn()

    def action_next_player(self) -> None:
        self.game.next_player()

    def action_toggle_help(self):
        """Show or hide the help dialog."""
        if not self.help_dialog:
            self._show_help_dialog()
        else:
            self._hide_help_dialog()

    async def on_key(self, event: events.Key):
        """Close help dialog on Esc key."""
        if event.key == "escape":
            if self.help_dialog:
                self._hide_help_dialog()
            event.stop()

    #
    # Model update callbacks
    #
    def update_pool(self, pool: Pool):
        """Update the player widget to show the current pool."""
        if self.debug_mode:
            # Make sure to reset the pool to trigger reactivity
            self.pool_widget.pool = Pool()
            self.pool_widget.pool = pool

    def update_player(self, player: Player):
        """Update the player widget to show the current player's hand."""
        # Make sure to reset the player to trigger reactivity
        self.player_widget.player = player

    def update_table(self, table: Table):
        """Update the table widget to show the current table."""
        # self.table_widget.table = Table()
        self.table_widget.table = table

    def set_status(self, message: str):
        """Set the status message."""
        self.status_widget.update(message)

    #
    # Private methods
    #
    def _show_help_dialog(self):
        """Show the help dialog."""
        self.help_dialog = MarkdownDialog("Help", HELP)
        self.mount(self.help_dialog)

    def _hide_help_dialog(self):
        """Hide the help dialog."""
        self.help_dialog.remove()
        self.help_dialog = None


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Play a command-line Rummikub game.")
    parser.add_argument('--debug', action="store_true", required=False, help='Display all players hand.', default=False)
    parser.add_argument('--total-players', type=int, required=False, help='Total number of players (2-4)', default=2)
    parser.add_argument('--human-players', type=int, required=False, help='Number of human players (0 to total)', default=0)
    parser.add_argument('--seed', type=int, required=False, help='The seed of the game')
    parser.add_argument('--strategy', choices=['highest-value', 'longest-run', 'all-in'], default='all-in', help='Strategy for computer players')
    parser.add_argument('--stop-at-turn', type=int, required=False,  help='If specified print a debug message that can be used to break on the debugger. Not stopping if -1', default=-1)
    parser.add_argument('--sort-table', action="store_true", required=False, help='Sort melds when displaying table.', default=False)
    args = parser.parse_args()
    return args

if __name__ == '__main__':

    args = parse_args()

    if not (2 <= args.total_players <= 4):
        print(emojize(":cross_mark: Argument 'total-players' must be between 2 and 4."))
        exit(1)

    if not (args.human_players <= args.total_players):
        print(emojize(":cross_mark: Argument 'human-players' must be less or equal to 'total-players'."))
        exit(1)

    app = RummikubApp(args)
    print(app.run())
