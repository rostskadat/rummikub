import itertools
import random

from collections import UserList
from enum import Enum
from typing import List

MAX_NUMBER_OF_TILES = 14
TILE_NUMBERS = list(range(1, MAX_NUMBER_OF_TILES))

class TileColor(Enum):
    RED = 1
    BLUE = 2
    ORANGE = 3
    BLACK = 4

    def __init__(self, order):
        self.order = order

class Tile:
    """
    Represents a Rummikub tile, which can be a numbered tile of a specific color or a joker.
    Attributes:
        `is_joker` (bool): Indicates if the tile is a joker.
        `number` (int): The number on the tile (ignored if `is_joker` is True).
        `color` (str): The color of the tile (ignored if `is_joker` is True).
    Methods:
        __str__(): Returns the string representation of the tile.
        __repr__(): Returns the string representation of the tile.
    """
    def __init__(self, is_joker, number: int = -1, color: TileColor|None = None):
        self.is_joker = is_joker
        self.number = number
        self.color = color

    def __str__(self):
        return f"[{self.number:02d}]" if not self.is_joker else '[JO]'

    def __repr__(self):
        return str(self)

    def __eq__(self, other):
        if not isinstance(other, Tile):
            return NotImplemented
        return (self.is_joker, self.number, self.color) == (other.is_joker, other.number, other.color)

    def __lt__(self, other):
        if not isinstance(other, Tile):
            return NotImplemented
        # Rule 1: Joker is always greater
        if self.is_joker and not other.is_joker:
            return False
        if not self.is_joker and other.is_joker:
            return True
        if self.is_joker and other.is_joker:
            return False  # Jokers are equal for sorting

        # Rule 2: Compare color order
        if self.color != other.color:
            return self.color.order < other.color.order

        # Rule 3: Compare number
        return self.number < other.number

    def __hash__(self):
        return hash((self.is_joker, self.number, self.color))

class Pool(UserList):

    def initialize(self) -> None:
        self.extend([Tile(False, num, color) for num, color in itertools.product(TILE_NUMBERS, TileColor)] * 2)
        self.append(Tile(True))
        self.append(Tile(True))

    def shuffle(self, seed:int|None = None) -> None:
        random.Random(seed).shuffle(self)

class Player(UserList):

    """
    Represents a player in the Rummikub game.

    Attributes:
        `name` (str): The name of the player.
        `tiles` (List[Tile]): The list of tiles currently held by the player.
        `is_human` (bool): Indicates if the player is a human or an AI.
        `has_played_initial` (bool): Tracks if the player has made their initial move.

    Methods:
        __init__(name: str, is_human: bool = True):
            Initializes a Player with a name and type (human or AI).
        draw_tile(pool: List[Tile]):
            Draws a tile from the pool and adds it to the player's tiles.
    """
    def __init__(self, name: str = "Anonymous", is_human: bool = True):
        super().__init__([])
        self.name = name
        self.is_human = is_human
        self.has_played_initial = False

    #
    # Properties
    #
    @property
    def hand(self):
        """Getter for hand"""
        return self.data

    @hand.setter
    def hand(self, tiles: List[Tile] = []):
        """Setter for hand"""
        self.data[:] = tiles

    def draw_tile(self, pool: Pool) -> None:
        assert pool
        self.append(pool.pop())

    def remove_meld(self, meld:List[Tile]) -> None:
        for tile in meld:
            self.remove(tile)

    def can_play(self) -> bool:
        return True if len(self) > 0 else False


class Table(UserList):

    def __init__(self, keep_sorted: bool = True):
        super().__init__()
        self.keep_sorted = keep_sorted

    def add_meld(self, meld):
        self.append(meld)

    def sort_table(self):
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

        self.sort(key=meld_key)

