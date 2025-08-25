from rummikub.objects import Player, TILE_NUMBERS, Pool, Table, Tile, TileColor
from textual.app import ComposeResult
from textual.containers import Horizontal, HorizontalScroll, Vertical, VerticalScroll
from textual.reactive import reactive
from textual.widgets import Static
from typing import List

class TileWidget(Static):
    """A simple widget to display a Tile with its number and color.

    NOTE: the color is taken care of by CSS classes.
    """
    def __init__(self, tile: Tile):
        super().__init__(str(tile), classes=f"tile-{'joker' if tile.is_joker else tile.color.name.lower()}", markup=False)

class MeldWidget(Horizontal):
    """A vertical scrollable view to display the table melds."""
    tiles: reactive[List[Tile]] = reactive([])

    def __init__(self, tiles:List[Tile], classes:str = "default"):
        super().__init__(classes=classes)
        self.tiles = tiles

    def compose(self) -> ComposeResult:
        for tile in self.tiles:
            yield TileWidget(tile)

class PoolWidget(Vertical):
    """A vertical scrollable view to display the pool tiles.

    Reactive attribute: pool: The current Pool object whose tiles are being displayed.
    """
    description: reactive[str] = reactive("Table:")
    tiles: reactive[List[Tile]] = reactive([])

    def __init__(self):
        super().__init__()

    def compose(self) -> ComposeResult:
        self.description_view = Static(f"Pool")
        self.tiles_view = HorizontalScroll(classes="pool-tiles")
        yield self.description_view
        yield self.tiles_view

    def watch_description(self, description:str):
        """Called when the description attribute changes."""
        self.description_view.update(description)

    def watch_tiles(self, tiles: Pool):
        """Called when the tiles attribute changes."""
        self.tiles_view.remove_children()
        self.tiles_view.mount_all([TileWidget(tile) for tile in reversed(tiles) ])

    @property
    def pool(self) -> Pool:
        return None

    @pool.setter
    def pool(self, tiles:Pool) -> None:
        self.description = f"Pool ({len(tiles)}/{len(TILE_NUMBERS)*len(TileColor)*2+2}):"
        self.tiles = [*tiles]

class PlayerWidget(Vertical):
    """The player widget displays the current player's name and hand.

    Reactive attribute: player: The current Player object whose hand is being displayed.
    """
    decsription : reactive[str|None] = reactive("Anonymous")
    hand : reactive[List[Tile]|None] = reactive([])

    def __init__(self):
        super().__init__()

    def compose(self) -> ComposeResult:
        self.decsription_view = Static(f"Player")
        self.hand_view = Vertical(classes="player-hand")
        yield self.decsription_view
        yield self.hand_view

    def watch_decsription(self, description: str):
        """Called when the description attribute changes."""
        self.decsription_view.update(description)

    def watch_hand(self, hand: List[Tile]):
        """called when the player_hand attribute changes."""
        self.hand_view.remove_children()
        for i, row in enumerate(self._get_tiles_in_rows(self.hand)):
            row_widget = HorizontalScroll(classes=f"tile-row tile-row-{i}")
            self.hand_view.mount(row_widget)
            row_widget.mount_all([TileWidget(tile) for tile in row ])

    @property
    def player(self) -> Player:
        return None

    @player.setter
    def player(self, player:Player) -> None:
        self.decsription = f"Player {player.name}'s hand ({len(player.hand)} tiles):"
        self.hand = [*player.hand]

    def _get_tiles_in_rows(self, hand: List[Tile]) -> List[List[Tile]]:
        """Organize the player's hand into rows grouped by color and sorted by number."""
        # Group tiles by color
        color_groups = {}
        for tile in hand:
            color = tile.color if not tile.is_joker else "joker"
            color_groups.setdefault(color, []).append(tile)
        # Sort each color group by tile number
        rows = []
        for color in sorted(color_groups.keys(), key=lambda c: c.order if c != "joker" else 5):
            rows.append(sorted(color_groups[color], key=lambda t: t.number if not t.is_joker else float('inf')))
        return rows

class TableWidget(Vertical):
    """A vertical view to display the table melds.

    Reactive attribute: table: The current Table object whose melds are being displayed.
    """
    description: reactive[str] = reactive("Table:")
    melds: reactive[List[List[Tile]]] = reactive([])

    def __init__(self, keep_sorted:bool = True):
        super().__init__()
        self.keep_sorted = keep_sorted

    def compose(self) -> ComposeResult:
        self.description_view = Static(f"Table:")
        self.melds_view = VerticalScroll(classes="table-melds")
        yield self.description_view
        yield self.melds_view

    def watch_description(self, description:str):
        self.description_view.update(description)

    def watch_melds(self, melds: Table):
        self.melds_view.remove_children()
        self.melds_view.mount_all([MeldWidget(meld) for meld in melds ])
        self.melds_view.styles.overflow_y = "auto"

    @property
    def table(self) -> Table:
        return None

    @table.setter
    def table(self, melds:Table) -> None:
        self.description = f"Table ({len(melds)} melds):"
        self.melds = [*melds]

    def _sort_table(self, melds:List[List[Tile]]):
        """
        Sorts table so that:
        1. Melds with the lowest total value come first.
        2. If equal, the shortest meld comes first.
        3. If still equal, the meld with the lowest tile (by number) comes first.
        """
        def meld_key(meld):
            meld_value = sum(tile.number for tile in meld if not tile.is_joker)
            meld_length = len(meld)
            # Find the lowest tile (by number, color, is_joker)
            lowest_tile = min(
                (tile.number for tile in meld),
                default=float('inf')
            )
            return (meld_value, meld_length, lowest_tile)

        melds.sort(key=meld_key)
        return melds
